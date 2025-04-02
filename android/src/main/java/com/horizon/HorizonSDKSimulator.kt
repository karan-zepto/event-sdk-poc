package com.horizon

import com.horizon.domain.EventDispatcher
import com.horizon.domain.EventManager
import com.horizon.domain.EventProcessor
import com.horizon.entity.Event
import com.horizon.entity.HorizonConfig
import com.horizon.network.FakeNetworkClient
import com.horizon.storage.InMemoryStorage
import com.horizon.utility.FakeNetworkMonitor
import com.horizon.utility.PrintLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object HorizonSDKSimulator {

  fun test() {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val config = HorizonConfig(30, 30, 2000, 500)
    val logger = PrintLogger()

    logger.log("HorizonSDKSimulator", "HorizonSDKSimulator started")

    val storage = InMemoryStorage()
    val networkClient = FakeNetworkClient(logger)
    val networkMonitor = FakeNetworkMonitor()
    val processor = EventProcessor(config, networkClient, storage,logger)
    val dispatcher = EventDispatcher(config, processor, storage, scope, networkMonitor, logger)
    val manager = EventManager(scope, storage, dispatcher, logger)

    manager.initialise()

    scope.launch {
      for (i in 1..10000) {
        delay((Math.random() * 16).toLong())
        manager.track(Event("$i"))
      }

      manager.shutdown()

      while(true){
        delay(1000)
        networkClient.printItems()
      }
    }
  }
}
