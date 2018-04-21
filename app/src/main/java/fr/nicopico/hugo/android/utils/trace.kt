package fr.nicopico.hugo.android.utils

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace

inline fun trace(name: String, block: Trace.() -> Unit) {
    FirebasePerformance.startTrace(name)
            .apply(block)
            .also {
                it.stop()
            }
}