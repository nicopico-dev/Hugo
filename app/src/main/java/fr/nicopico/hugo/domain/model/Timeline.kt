package fr.nicopico.hugo.domain.model

import com.google.firebase.perf.FirebasePerformance
import fr.nicopico.hugo.android.utils.trace
import fr.nicopico.hugo.domain.utils.onlyDate
import java.util.*

class Timeline(private val entries: List<Entry> = emptyList()) {

    val sections: List<Section>

    init {
        val trace = FirebasePerformance.startTrace("Timeline.sections")
        trace.incrementCounter("entries", entries.size.toLong())
        sections = entries
                .sortedByDescending { it.time }
                .groupBy { it.time.onlyDate() }
                .map { (date, entries) ->
                    Section(date, entries)
                }
        trace.stop()
    }

    operator fun plus(entry: Timeline.Entry) = Timeline(entries + entry)
    operator fun minus(entry: Timeline.Entry) = Timeline(entries - entry)

    fun replace(updatedEntry: Timeline.Entry): Timeline {
        val updatedEntries = entries.map {
            if (it.remoteId == updatedEntry.remoteId) {
                updatedEntry
            } else {
                it
            }
        }
        return Timeline(updatedEntries)
    }

    override fun toString(): String {
        return "Timeline{${this.hashCode()} ${entries.size} entries}"
    }

    data class Section(val time: Date, val entries: List<Entry>) {
        val totalMilk by lazy {
            trace("Section.totalMilk") {
                incrementCounter("entries", entries.size.toLong())
                entries.filter { it.type == CareType.FOOD }
                        .flatMap { it.cares }
                        .filter { it is BottleFeeding }
                        .sumBy { (it as BottleFeeding).volume }
            }
        }
    }

    data class Entry(
            val remoteId: String?,
            val type: CareType,
            val time: Date,
            val cares: List<Care>,
            val notes: String? = null
    ) {
        constructor(type: CareType, time: Date, cares: List<Care>, notes: String? = null)
                : this(null, type, time, cares, notes)

        init {
            require(cares.all { it.type == type })
        }
    }
}