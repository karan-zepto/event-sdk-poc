package com.horizon.storage

import com.horizon.entity.EventStatus

data class EventEntity(
  val eventId: String,
  val name: String,
  val properties: String,
  val batchId: Int = -1,
  val timestamp: Long,
  val failureCount: Int,
  val status: EventStatus
) {

  override fun toString(): String {
    return "EventEntity(eventId='$eventId', batchId=$batchId, failureCount=$failureCount)"
  }
}
