package com.horizon.entity

data class EventBatch(
  val id: String,
  val events: List<Event>
)
