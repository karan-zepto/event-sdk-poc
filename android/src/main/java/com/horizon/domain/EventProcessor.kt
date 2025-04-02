package com.horizon.domain

import com.horizon.entity.EventBatch
import com.horizon.entity.HorizonConfig
import com.horizon.network.NetworkClient
import com.horizon.storage.EventStorage
import com.horizon.utility.BatchHelper

class EventProcessor(
  private val config: HorizonConfig,
  private val networkClient: NetworkClient,
  private val eventStorage: EventStorage
) {

  suspend fun processBatch(): Boolean {
    var eventsToSend = eventStorage.getPendingEvents(config.batchSize)

    if (eventsToSend.isEmpty()) return false

    val batchId = BatchHelper.nextBatchId()

    eventsToSend = eventsToSend.map { event ->
      event.copy(batchId = batchId)
    }

    val batch = EventBatch(
      batchId,
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
    val eventsToRetry = eventStorage.getFailedEvents(config.batchSize)

    if (eventsToRetry.isEmpty()) return false

    val batch = EventBatch(BatchHelper.nextBatchId(), eventsToRetry)

    val result = networkClient.sendEvents(batch)

    return result.onSuccess {
      eventStorage.removeEvents(eventsToRetry)
    }.onFailure {
      handleFailedBatch(batch)
    }.isSuccess
  }

  private suspend fun handleFailedBatch(batch: EventBatch) {
    eventStorage.markEventsFailed(batch.events)
    eventStorage.removeEvents(batch.events)
  }
}
