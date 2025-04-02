package com.horizon.entity

import com.horizon.storage.EventEntity

data class EventBatch(
  val events: List<EventEntity>
)
