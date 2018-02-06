package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.Timeline
import redux.api.Reducer

val babyReducer = Reducer<AppState> { state, action ->
    when (action) {
        is BABY_ADDED -> state.copy(babies = state.babies + action.baby)
        is BABY_MODIFIED -> {
            val index = state.babies.indexOf(action.baby)
            val updatedBabies = state.babies.toMutableList().apply {
                removeAt(index)
                add(index, action.baby)
            }
            state.copy(babies = updatedBabies)
        }
        is BABY_REMOVED -> state.copy(babies = state.babies - action.baby)
        is SELECT_BABY -> state.copy(selectedBaby = action.baby)
        else -> state
    }
}

val timelineReducer = Reducer<AppState> { state, action ->
    when (action) {
        is ENTRY_ADDED -> state.copy(timeline = state.timeline + action.entry)
        is ENTRY_MODIFIED -> {
            val index = state.timeline.indexOf(action.entry)
            val updatedTimeline = state.timeline.toMutableList().apply {
                removeAt(index)
                add(index, action.entry)
            }
            state.copy(timeline = Timeline(updatedTimeline))
        }
        is ENTRY_REMOVED -> state.copy(timeline = state.timeline - action.entry)
        else -> state
    }
}

val remoteReducer = Reducer<AppState> { state, action ->
    when (action) {
        is AUTHENTICATED -> state.copy(user = action.user)
        FETCH_BABIES -> state.copy(babies = emptyList())
        FETCH_TIMELINE -> state.copy(timeline = Timeline())
        else -> state
    }
}