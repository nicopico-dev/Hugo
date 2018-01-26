@file:Suppress("ClassName")

package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.Timeline
import redux.api.Reducer

val timelineReducer = Reducer<AppState> { state, action ->
    when (action) {
        is ADD_ENTRY -> {
            state.copy(timeline = state.timeline + action.entry)
        }
        is UPDATE_ENTRY -> {
            val index = state.timeline.indexOf(action.oldEntry)
            val updatedTimeline = state.timeline.toMutableList().apply {
                removeAt(index)
                add(index, action.newEntry)
            }
            state.copy(timeline = updatedTimeline)
        }
        is REMOVE_ENTRY -> {
            state.copy(timeline = state.timeline - action.entry)
        }
        is ADD_ENTRIES -> {
            state.copy(timeline = state.timeline + action.entries)
        }
        else -> state
    }
}

data class ADD_ENTRY(val entry: Timeline.Entry)
data class UPDATE_ENTRY(val oldEntry: Timeline.Entry, val newEntry: Timeline.Entry)
data class REMOVE_ENTRY(val entry: Timeline.Entry)
data class ADD_ENTRIES(val entries: List<Timeline.Entry>)