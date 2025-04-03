package com.horizon.entity

data class HorizonConfig(
  val maxParallelRequests: Int,
  val batchSize: Int,
  val maxBatchSize: Int,
  val batchIntervalInMs: Long,
  val retryIntervalInMs: Long
)
