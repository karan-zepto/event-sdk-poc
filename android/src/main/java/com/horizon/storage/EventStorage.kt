package com.horizon.storage

import com.horizon.entity.Event
import kotlinx.coroutines.flow.Flow

interface EventStorage {
  val eventCount: Flow<Int>
  val hasFailedEvents: Flow<Boolean>

  suspend fun storeEvent(event: Event)

  suspend fun updateBatchIdForEvents(eventIds: List<String>, batchId: Int)

  suspend fun getPendingEvents(limit: Int): List<EventEntity>

  suspend fun removeEvents(events: List<EventEntity>)

  suspend fun markEventsFailed(events: List<EventEntity>)

  suspend fun getFailedEvents(limit: Int): List<EventEntity>
}
