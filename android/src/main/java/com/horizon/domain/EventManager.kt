package com.horizon.domain

import com.horizon.entity.Event
import com.horizon.storage.EventStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class EventManager(
  private val eventStorage: EventStorage,
  private val eventDispatcher: EventDispatcher
) {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  fun initialise() {
    eventDispatcher.initialize()
  }

  fun track(event: Event) {
    scope.launch {
      eventStorage.storeEvent(event)
    }
  }

  fun shutdown() {
    scope.cancel()
  }
}
