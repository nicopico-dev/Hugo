package fr.nicopico.hugo.domain.model

import fr.nicopico.hugo.domain.services.trace
import fr.nicopico.hugo.domain.utils.onlyDate
import java.util.*

class Timeline(private val entries: List<Entry> = emptyList()) {

    val sections: List<Section>

    init {
        sections = trace("Timeline.sections") {
            incrementCounter("entries", entries.size.toLong())
            entries
                    .sortedByDescending { it.time }
                    .groupBy { it.time.onlyDate() }
                    .map { (date, entries) ->
                        Section(date, entries)
                    }
        }
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
        val totalMilk: Int by lazy {
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