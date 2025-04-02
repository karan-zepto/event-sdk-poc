package com.horizon.entity

import com.horizon.storage.EventEntity

data class EventBatch(
  val id: Int,
  val events: List<EventEntity>
)
