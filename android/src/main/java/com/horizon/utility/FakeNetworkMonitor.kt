package com.horizon.utility

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FakeNetworkMonitor(
  private val scope: CoroutineScope,
  private val logger: PrintLogger
) : NetworkMonitor {

  private val flow : MutableStateFlow<Boolean> = MutableStateFlow(true)
  override val isConnected: StateFlow<Boolean> = flow

  init {
    scope.launch {
      while(true){
        logger.log("FakeNetworkMonitor", "FakeNetworkMonitor ${if(flow.value) "simulate new issues" else "simulate working internet"}")
        flow.emit(!flow.value)
        delay(3000)
      }
    }
  }
}
