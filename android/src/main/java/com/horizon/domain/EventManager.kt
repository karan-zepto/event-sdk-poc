package com.horizon.domain

import com.horizon.entity.Event
import com.horizon.storage.EventStorage
import com.horizon.utility.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class EventManager(
  private val scope: CoroutineScope,
  private val eventStorage: EventStorage,
  private val eventDispatcher: EventDispatcher,
  private val logger: Logger
) {

  fun initialise() {
    logger.log("EventManager", "Initialising EventManager")
    eventDispatcher.initialize()
  }

  fun track(event: Event) {
    scope.launch {
      //logger.log("EventManager", "Tracking event: ${event.name}")
      eventStorage.storeEvent(event)
    }
  }

  fun shutdown() {
    logger.log("EventManager", "Shutting down EventManager")
    eventDispatcher.shutdown()
    //scope.cancel()
  }
}
