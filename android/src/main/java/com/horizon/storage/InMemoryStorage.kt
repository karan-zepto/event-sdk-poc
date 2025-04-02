package com.horizon.storage

import com.horizon.entity.Event
import com.horizon.entity.EventStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class InMemoryStorage(
  private val coroutineScope: CoroutineScope
) : EventStorage {
  private val mutex = Mutex()

  private val events = MutableStateFlow<List<EventEntity>>(emptyList())

  override val eventCount: StateFlow<Int>
    get() = events.map { it.size }
      .stateIn(coroutineScope, initialValue = 0, started = SharingStarted.Eagerly)

  override val hasFailedEvents: StateFlow<Boolean>
    get() = events.map { it.any { event -> event.status == EventStatus.FAILED } }
      .stateIn(coroutineScope, initialValue = false, started = SharingStarted.Eagerly)

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
            event.copy(batchId = batchId)
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
      }.sortedBy { event -> event.failureCount }.take(limit)
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
