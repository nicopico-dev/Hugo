package fr.nicopico.hugo.android.utils

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace

inline fun <T> trace(name: String, block: Trace.() -> T): T {
    val trace = FirebasePerformance.startTrace(name)
    val result = trace.let(block)
    return result.also { trace.stop() }
}