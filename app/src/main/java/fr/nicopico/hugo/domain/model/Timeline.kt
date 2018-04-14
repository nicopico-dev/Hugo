package fr.nicopico.hugo.domain.model

import java.util.*

class Timeline(entries: List<Entry> = emptyList()) {

    val entries = entries.sortedByDescending { it.time }

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
}