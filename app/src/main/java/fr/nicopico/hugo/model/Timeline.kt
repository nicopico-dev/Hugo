package fr.nicopico.hugo.model

import java.text.SimpleDateFormat
import java.util.*

object Timeline {
    data class Entry(val type: CareType, val time: Date, val cares: List<Care>, val notes: String? = null) {
        init {
            require(cares.all { it.type == type })
        }
    }
}

private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)

fun entry(time: String, vararg cares: Care, notes: String? = null): Timeline.Entry {
    return Timeline.Entry(cares.first().type, dateFormat.parse(time), cares.toList(), notes)
}