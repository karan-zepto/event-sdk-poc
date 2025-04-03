package com.horizon.entity

data class Event(
  val name: String,
  val properties: Map<String, Any> = emptyMap(),
)
