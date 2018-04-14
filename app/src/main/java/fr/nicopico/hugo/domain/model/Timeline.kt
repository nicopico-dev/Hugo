package fr.nicopico.hugo.domain.model

import fr.nicopico.hugo.domain.utils.onlyDate
import java.util.*

class Timeline(entries: List<Entry> = emptyList()) {

    @Deprecated("Use sections instead")
    val entries = entries.sortedByDescending { it.time }
    val sections = entries
            .groupBy { it.time.onlyDate() }
            .map { (date, entries) ->
                Section(date, entries.sortedByDescending { it.time })
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

    data class Section(val date: Date, val entries: List<Entry>) {
        val totalMilk by lazy {
            entries.filter { it.type == CareType.FOOD }
                    .flatMap { it.cares }
                    .filter { it is BottleFeeding }
                    .sumBy { (it as BottleFeeding).volume }
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