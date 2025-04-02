package com.horizon.storage

import com.horizon.entity.EventStatus

data class EventEntity(
  val eventId: String,
  val name: String,
  val batchId: Int = -1,
  val timestamp: Long,
  val failureCount: Int,
  val status: EventStatus
)
