package fr.nicopico.hugo.model

import java.text.SimpleDateFormat
import java.util.*

class Timeline(
        private val entries: List<Entry> = emptyList()
) : List<Timeline.Entry> by entries {
    data class Entry(val type: CareType, val time: Date, val cares: List<Care>, val notes: String? = null) {
        init {
            require(cares.all { it.type == type })
        }
    }

    operator fun plus(entry: Timeline.Entry) = Timeline(entries + entry)
    operator fun minus(entry: Timeline.Entry) = Timeline(entries - entry)
}

private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)

fun entry(time: String, vararg cares: Care, notes: String? = null): Timeline.Entry {
    return Timeline.Entry(cares.first().type, dateFormat.parse(time), cares.toList(), notes)
}