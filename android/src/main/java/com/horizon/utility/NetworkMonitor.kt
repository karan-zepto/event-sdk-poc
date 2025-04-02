package com.horizon.utility

import kotlinx.coroutines.flow.StateFlow

interface NetworkMonitor {
  val isConnected: StateFlow<Boolean>
}
