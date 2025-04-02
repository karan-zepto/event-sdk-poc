package com.horizon.domain

import com.horizon.entity.EventBatch
import com.horizon.entity.HorizonConfig
import com.horizon.network.NetworkClient
import com.horizon.storage.EventStorage
import java.util.UUID

class EventProcessor(
  private val config: HorizonConfig,
  private val networkClient: NetworkClient,
  private val eventStorage: EventStorage
) {

  suspend fun processBatch(): Boolean {
    val eventsToSend = eventStorage.getPendingEvents(config.batchSize)

    if (eventsToSend.isEmpty()) return false

    val batch = EventBatch(
      UUID.randomUUID().toString(),
      eventsToSend
    )

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

    val batch = EventBatch(UUID.randomUUID().toString(), eventsToRetry)

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
