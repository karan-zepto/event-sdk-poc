package com.horizon.domain

import com.horizon.utility.NetworkMonitor
import com.horizon.entity.HorizonConfig
import com.horizon.storage.EventStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EventDispatcher(
  private val config: HorizonConfig,
  private val eventProcessor: EventProcessor,
  private val eventStorage: EventStorage,
  private val scope: CoroutineScope,
  private val networkMonitor: NetworkMonitor
) {

  private val processBatchTrigger = Channel<Unit>(Channel.CONFLATED)
  private val batchTriggerFlow = processBatchTrigger.receiveAsFlow()

  private val failedBatchTrigger = Channel<Unit>(Channel.CONFLATED)
  private val failedBatchTriggerFlow = failedBatchTrigger.receiveAsFlow()

  fun initialize() {
    observeBatchTrigger()
    observeFailedTrigger()

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
      }.distinctUntilChanged().filter { it }.collect {
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
        batchTriggerFlow.collect {
          eventProcessor.processBatch()
        }
      }
    }
  }

  private fun observeFailedTrigger() {
    scope.launch {
      failedBatchTriggerFlow.collect {
        while (true) {
          val success = eventProcessor.processFailedEvents()

          if (!success) {
            delay(config.retryIntervalInMs)
          }

          if (!eventStorage.hasFailedEvents.value || !networkMonitor.isConnected.value) {
            break
          }
        }
      }
    }
  }

  private fun setupEventCountTrigger() {
    scope.launch {
      eventStorage.eventCount
        .filter { it >= config.batchSize }
        .collect {
          triggerBatchProcessing()
        }
    }
  }

  @OptIn(FlowPreview::class)
  private fun setupTimerTrigger() {
    scope.launch {
      eventStorage.eventCount
        .filter { it > 0 } // Only when we have events
        .debounce(config.batchIntervalInMs)
        .collect {
          triggerBatchProcessing()
        }
    }
  }
}
