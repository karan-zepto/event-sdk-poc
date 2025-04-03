package com.horizon.utils

import android.util.Log
import com.horizon.utility.PrintLogger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object Tracer {
  private val mutex = Mutex()
  private val traces = mutableMapOf<String, TraceData>()
  private val logger = PrintLogger()

  private data class TraceData(
    var startTime: Long = 0L,
    var totalTime: Long = 0L,
    var isRunning: Boolean = false
  )

  suspend fun start(id: String) {
    mutex.withLock {
      val trace = traces.getOrPut(id) { TraceData() }
      if (!trace.isRunning) {
        trace.startTime = System.nanoTime()
        trace.isRunning = true
      }
    }
  }

  suspend fun end(id: String) {
    mutex.withLock {
      traces[id]?.let { trace ->
        if (trace.isRunning) {
          val duration = System.nanoTime() - trace.startTime
          trace.totalTime += duration
          trace.isRunning = false

//          logger.log(
//            "Tracer", "Operation '$id' took ${duration / 1_000_000.0}ms " +
//              "(Total: ${trace.totalTime / 1_000_000.0}ms)"
//          )
        }
      }
    }
  }

  suspend fun reset(id: String) {
    mutex.withLock {
      traces.remove(id)
    }
  }

  suspend fun resetAll() {
    mutex.withLock {
      traces.clear()
    }
  }

  suspend fun printStats() {
    mutex.withLock {
      if (traces.isEmpty()) {
        logger.log("Tracer", "No traces recorded")
        return
      }

      val stats = StringBuilder("\nTracer Statistics:\n")
      stats.append("================\n")

      traces.entries.sortedByDescending { it.value.totalTime }.forEach { (id, data) ->
        stats.append(
          String.format(
            "%-30s Total: %8.2fms %s\n",
            id,
            data.totalTime / 1_000_000.0,
            if (data.isRunning) "(Running)" else ""
          )
        )
      }
      stats.append("================")

      logger.log("Tracer", stats.toString())
    }
  }
}
