package com.horizon.storage

import com.horizon.entity.Event
import com.horizon.entity.EventStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class InMemoryStorage : EventStorage {
  private val mutex = Mutex()

  private val events = MutableStateFlow<List<EventEntity>>(emptyList())
  override val eventCount: Flow<Int> = events.map { it.filter { it.status == EventStatus.PENDING }.size }.distinctUntilChanged()
  override val hasFailedEvents: Flow<Boolean> = events.map { it.any { event -> event.status == EventStatus.FAILED } }.distinctUntilChanged()

  override suspend fun storeEvent(event: Event) {
    mutex.withLock {
      events.update { list ->
        list + EventEntity(
          eventId = UUID.randomUUID().toString(),
          name = event.name,
          timestamp = System.currentTimeMillis(),
          failureCount = 0, status = EventStatus.PENDING
        )
      }
    }
  }

  override suspend fun updateBatchIdForEvents(eventIds: List<String>, batchId: Int) {
    mutex.withLock {
      events.update { list ->
        list.map { event ->
          if (eventIds.contains(event.eventId)) {
            event.copy(batchId = batchId, status = EventStatus.IN_PROGRESS)
          } else {
            event
          }
        }
      }
    }
  }

  override suspend fun getFailedEvents(limit: Int): List<EventEntity> {
    return mutex.withLock {
      events.value.filter { event ->
        event.status == EventStatus.FAILED
      }.sortedBy { event -> event.failureCount }.sortedBy { it.batchId }.take(limit)
    }
  }

  override suspend fun getPendingEvents(limit: Int): List<EventEntity> {
    return mutex.withLock {
      events.value.filter { event ->
        event.status == EventStatus.PENDING
      }.take(limit)
    }
  }

  override suspend fun markEventsFailed(events: List<EventEntity>) {
    mutex.withLock {
      val ids = events.map { it.eventId }.toSet()

      this.events.update { list ->
        list.map { event ->
          if (ids.contains(event.eventId)) {
            event.copy(
              failureCount = event.failureCount + 1,
              status = EventStatus.FAILED
            )
          } else {
            event
          }
        }
      }
    }
  }

  override suspend fun removeEvents(events: List<EventEntity>) {
    mutex.withLock {
      val ids = events.map { event -> event.eventId }.toSet()
      this.events.update { list ->
        list.filter { event -> !ids.contains(event.eventId) }
      }
    }
  }
}
