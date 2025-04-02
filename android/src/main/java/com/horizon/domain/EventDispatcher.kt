package com.horizon.domain

import com.horizon.utility.NetworkMonitor
import com.horizon.entity.HorizonConfig
import com.horizon.storage.EventStorage
import com.horizon.utility.PrintLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.math.log

class EventDispatcher(
  private val config: HorizonConfig,
  private val eventProcessor: EventProcessor,
  private val eventStorage: EventStorage,
  private val scope: CoroutineScope,
  private val networkMonitor: NetworkMonitor,
  private val logger: PrintLogger
) {

  private val processBatchTrigger = Channel<Unit>(Channel.CONFLATED)
  private val failedBatchTrigger = Channel<Unit>(Channel.CONFLATED)
  private val combined =
    merge(processBatchTrigger.receiveAsFlow().map { EventProcessorSignal.PROCESS_BATCH },
      failedBatchTrigger.receiveAsFlow().map { EventProcessorSignal.PROCESS_FAILED_BATCH })

  fun initialize() {
    observeBatchTrigger()

    setupRetryProcessing()

    setupEventCountTrigger()
    setupTimerTrigger()
  }

  private fun setupRetryProcessing() {
    scope.launch {
      combine(
        eventStorage.hasFailedEvents,
        networkMonitor.isConnected,
      ) { hasFailedEvents, isConnected ->
        hasFailedEvents && isConnected
      }.distinctUntilChanged().filter { it }.debounce(config.retryIntervalInMs).collect {
        triggerFailedBatchProcessing()
      }
    }
  }

  private fun triggerBatchProcessing() {
    processBatchTrigger.trySend(Unit)
  }

  private fun triggerFailedBatchProcessing() {
    failedBatchTrigger.trySend(Unit)
  }

  private fun observeBatchTrigger() {
    for (i in 1..config.maxParallelRequests) {
      scope.launch {
        logger.log("EventDispatcher", "Batch Observer setup $i")

        combined.collect { signal ->
          when (signal) {
            EventProcessorSignal.PROCESS_BATCH -> eventProcessor.processBatch()
            EventProcessorSignal.PROCESS_FAILED_BATCH -> {
              while (true) {
                logger.log("EventDispatcher", "Failed Batch Observer process")
                val success = eventProcessor.processFailedEvents()

                if (!success) {
                  delay(config.retryIntervalInMs)
                }

                if (!eventStorage.hasFailedEvents.first() || !networkMonitor.isConnected.value) {
                  break
                }
              }
            }
          }

        }
      }
    }
  }

  private fun setupEventCountTrigger() {
    scope.launch {
      logger.log("EventDispatcher", "Counter setup")
      eventStorage.eventCount
        .filter {
          it >= config.batchSize
        }
        .collect {
          triggerBatchProcessing()
        }
    }
  }

  @OptIn(FlowPreview::class)
  private fun setupTimerTrigger() {
    scope.launch {
      logger.log("EventDispatcher", "Timer setup")
      eventStorage.eventCount
        .filter {
          it > 0
        } // Only when we have events
        .debounce(config.batchIntervalInMs)
        .collect {
          triggerBatchProcessing()
        }
    }
  }

  fun shutdown() {
    scope.launch {
      logger.log(
        "EventDispatcher",
        "Shutting down EventDispatcher, pending items ${eventStorage.eventCount.first()}"
      )
    }
  }
}
