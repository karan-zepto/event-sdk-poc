package com.horizon.domain

import com.horizon.entity.EventBatch
import com.horizon.entity.HorizonConfig
import com.horizon.network.NetworkClient
import com.horizon.storage.EventStorage
import com.horizon.utility.BatchHelper
import com.horizon.utility.PrintLogger
import kotlinx.coroutines.flow.first

class EventProcessor(
  private val config: HorizonConfig,
  private val networkClient: NetworkClient,
  private val eventStorage: EventStorage,
  private val logger: PrintLogger
) {

  suspend fun processBatch(): Boolean {
    var eventsToSend = eventStorage.getPendingEvents(getBatchSize())

    logger.log("EventProcessor", "Processing new events")
    if (eventsToSend.isEmpty()) return false

    val batchId = BatchHelper.nextBatchId()

    eventsToSend = eventsToSend.map { event ->
      event.copy(batchId = batchId)
    }

    val batch = EventBatch(
      eventsToSend
    )

    eventStorage.updateBatchIdForEvents(eventsToSend.map { it.eventId }, batchId)

    val result = networkClient.sendEvents(batch)

    return result.onSuccess {
      eventStorage.removeEvents(eventsToSend)
    }.onFailure {
      handleFailedBatch(batch)
    }.isSuccess
  }

  suspend fun processFailedEvents(): Boolean {
    val eventsToRetry = eventStorage.getFailedEvents(getBatchSize())

    logger.log("EventProcessor", "Processing failed events")
    if (eventsToRetry.isEmpty()) return false

    val batch = EventBatch(eventsToRetry)

    val result = networkClient.sendEvents(batch)

    return result.onSuccess {
      eventStorage.removeEvents(eventsToRetry)
    }.onFailure {
      handleFailedBatch(batch)
    }.isSuccess
  }

  private suspend fun handleFailedBatch(batch: EventBatch) {
    eventStorage.markEventsFailed(batch.events)
  }

  suspend fun getBatchSize(): Int {
    if(eventStorage.pendingCount.first() > config.batchSize * 6){
      return config.maxBatchSize
    }
    return config.batchSize
  }
}
