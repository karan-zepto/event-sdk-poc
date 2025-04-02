package com.horizon.utility

object BatchHelper {
  private var batchId = 0;

  fun nextBatchId(): Int {
    return batchId++
  }
}
