package com.horizon.utility

import android.util.Log

class PrintLogger : Logger {
  override fun log(tag: String, message: String) {
    Log.d("Karan", "${tag}: $message")
  }
}
