package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.Timeline
import redux.api.Reducer

val timelineReducer = Reducer<AppState> { state, action ->
    when (action) {
        is ENTRY_ADDED -> {
            state.copy(timeline = state.timeline + action.entry)
        }
        is ENTRY_MODIFIED -> {
            val index = state.timeline.indexOf(action.entry)
            val updatedTimeline = state.timeline.toMutableList().apply {
                removeAt(index)
                add(index, action.entry)
            }
            state.copy(timeline = Timeline(updatedTimeline))
        }
        is ENTRY_REMOVED -> {
            state.copy(timeline = state.timeline - action.entry)
        }
        else -> state
    }
}

val remoteReducer = Reducer<AppState> { state, action ->
    when (action) {
        is REQUEST_REMOTE_DATA -> state.copy(user = action.user, loading = true)
    // TODO Merge with local data ?
        is REMOTE_DATA_FETCHED -> state.copy(loading = false, timeline = action.timeline)
        else -> state
    }
}