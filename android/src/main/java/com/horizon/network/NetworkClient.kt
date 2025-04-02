package com.horizon.network

import com.horizon.entity.EventBatch

interface NetworkClient {
  suspend fun sendEvents(batch: EventBatch): Result<Unit>

}
