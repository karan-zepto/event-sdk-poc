package com.horizon.storage

import com.horizon.entity.Event
import kotlinx.coroutines.flow.StateFlow

interface EventStorage {
  val eventCount: StateFlow<Int>
  val hasFailedEvents: StateFlow<Boolean>

  suspend fun storeEvent(event: Event)

  suspend fun getPendingEvents(limit: Int): List<Event>

  suspend fun removeEvents(events: List<Event>)

  suspend fun markEventsFailed(events: List<Event>)

  suspend fun getFailedEvents(limit: Int): List<Event>
}
