package fr.nicopico.hugo.domain.services

import fr.nicopico.hugo.android.HugoLogger
import fr.nicopico.hugo.android.verbose

interface PerformanceService {
    fun startTrace(name: String): Trace
}

interface Trace {
    fun start()
    fun stop()
    fun incrementCounter(counterName: String, value: Long)
}

var performanceService: PerformanceService = DisabledPerformanceService

object DisabledPerformanceService : PerformanceService {
    override fun startTrace(name: String): Trace {
        return DisabledTrace(name)
    }
}

private class DisabledTrace(
        val name: String
) : Trace, HugoLogger {
    override fun start() {
        verbose { "Trace $name started" }
    }

    override fun stop() {
        verbose { "Trace $name stopped" }
    }

    override fun incrementCounter(counterName: String, value: Long) {
        verbose { "Setting $counterName to $value for trace $name" }
    }
}

inline fun <T> trace(name: String, block: Trace.() -> T): T {
    val trace = performanceService.startTrace(name)
    val result = trace.let(block)
    return result.also { trace.stop() }
}