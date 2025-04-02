package com.horizon.network

import com.horizon.entity.EventBatch
import com.horizon.utility.PrintLogger
import kotlinx.coroutines.delay

class FakeNetworkClient(
  private val logger: PrintLogger
) : NetworkClient {

  private val items = mutableListOf<Int>()

  override suspend fun sendEvents(batch: EventBatch): Result<Unit> {
    delay(300)

    if (Math.random() < 0.8) {
      logger.log(
        "FakeNetworkClient",
        "Trying to send batches: count = ${batch.events.size}, batches = ${
          batch.events.map { it.batchId }.distinct()
        }, events = ${batch.events.map { it.name }}, success"
      )
      items.addAll(batch.events.map { it.name.toInt() })
      return Result.success(Unit)
    }

    logger.log(
      "FakeNetworkClient",
      "Trying to send batches: count = ${batch.events.size}, batches = ${
        batch.events.map { it.batchId }.distinct()
      }, events = ${batch.events.map { it.name }}, failed"
    )
    return Result.failure(Exception("Failed to send events"))
  }


  fun printItems() {

    var sorted = items.sorted()
    var supposed = 10000
    var missing = mutableListOf<Int>()

    for(i in 1..supposed){
      if (!sorted.contains(i)){
        missing.add(i)
      }
    }
    logger.log("FakeNetworkClient", "missing items ${missing}")
  }
}
