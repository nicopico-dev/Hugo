package fr.nicopico.hugo.android.services

import com.google.firebase.perf.FirebasePerformance
import fr.nicopico.hugo.domain.services.PerformanceService
import fr.nicopico.hugo.domain.services.Trace
import fr.nicopico.hugo.domain.services.performanceService

object FirebasePerformanceService : PerformanceService {

    fun initialize() {
        performanceService = this
    }

    override fun startTrace(name: String): Trace {
        return FirebasePerformance
                .startTrace(name)
                .wrap()
    }
}

private fun com.google.firebase.perf.metrics.Trace.wrap() = FirebaseTrace(this)

private class FirebaseTrace(
        val trace: com.google.firebase.perf.metrics.Trace
) : Trace {
    override fun start() {
        trace.start()
    }

    override fun stop() {
        trace.stop()
    }

    override fun incrementCounter(counterName: String, value: Long) {
        trace.incrementCounter(counterName, value)
    }
}
