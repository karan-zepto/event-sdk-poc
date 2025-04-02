package com.horizon.utility

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeNetworkMonitor : NetworkMonitor {

  override val isConnected: StateFlow<Boolean> = MutableStateFlow(true)
}
