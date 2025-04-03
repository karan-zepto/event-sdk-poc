package com.horizon.storage

import com.horizon.entity.Event
import com.horizon.entity.EventStatus
import com.horizon.utils.Tracer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import java.util.UUID

class InMemoryStorage : EventStorage {
  private val mutex = Mutex()

  private val events = MutableStateFlow<List<EventEntity>>(emptyList())
  override val eventCount: Flow<Int> =
    events.map {
      return@map it.filter { it.status == EventStatus.PENDING }.size
    }.distinctUntilChanged()

  override val pendingCount: Flow<Int>
    get() = events.map { it.count { event -> event.status == EventStatus.FAILED } }.distinctUntilChanged()

  override val hasFailedEvents: Flow<Boolean> = pendingCount.map { it > 0 }.distinctUntilChanged()

  override suspend fun storeEvent(event: Event) {
    Tracer.start("storeEvent")
    mutex.withLock {
      events.update { list ->
        list + EventEntity(
          eventId = UUID.randomUUID().toString(),
          name = event.name,
          properties = event.properties.toJSON(),
          timestamp = System.currentTimeMillis(),
          failureCount = 0, status = EventStatus.PENDING
        )
      }
    }.also {
      Tracer.end("storeEvent")
    }
  }

  override suspend fun updateBatchIdForEvents(eventIds: List<String>, batchId: Int) {
    Tracer.start("updateBatchIdForEvents")
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
    }.also {
      Tracer.end("updateBatchIdForEvents")
    }
  }

  override suspend fun getFailedEvents(limit: Int): List<EventEntity> {
    Tracer.start("getFailedEvents")
    return mutex.withLock {
      events.value.filter { event ->
        event.status == EventStatus.FAILED
      }.sortedWith(
        compareBy<EventEntity> { it.batchId }
          .thenBy { it.failureCount }
      ).take(limit)
    }.also {
      Tracer.end("getFailedEvents")
    }
  }

  override suspend fun getPendingEvents(limit: Int): List<EventEntity> {
    Tracer.start("getPendingEvents")
    return mutex.withLock {
      events.value.filter { event ->
        event.status == EventStatus.PENDING
      }.take(limit)
    }.also {
      Tracer.end("getPendingEvents")
    }
  }

  override suspend fun markEventsFailed(events: List<EventEntity>) {
    Tracer.start("markEventsFailed")
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
    }.also {
      Tracer.end("markEventsFailed")
    }
  }

  override suspend fun removeEvents(events: List<EventEntity>) {
    Tracer.start("removeEvents")
    mutex.withLock {
      val ids = events.map { event -> event.eventId }.toSet()
      this.events.update { list ->
        list.filter { event -> !ids.contains(event.eventId) }
      }
    }.also {
      Tracer.end("removeEvents")
    }
  }

  private fun Map<String, Any>.toJSON(): String {
    val json = JSONObject()
    for ((key, value) in this) {
      json.put(key, value.toString())
    }
    return json.toString()
  }
}
