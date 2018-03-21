package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.Screen
import fr.nicopico.hugo.model.Timeline
import redux.api.Reducer

val navigationReducer = Reducer<AppState> { state, action ->
    when (action) {
        is AUTHENTICATED -> state.copy(
                // Go directly to the timeline if a baby is selected
                screen = state.selectedBaby?.let { Screen.Timeline } ?: Screen.BabySelection
        )
        is SELECT_BABY -> state.copy(screen = Screen.Timeline)
        UNSELECT_BABY -> state.copy(screen = Screen.BabySelection)
        GO_BACK -> state.copy(
                screen = when (state.screen) {
                    Screen.Timeline -> Screen.BabySelection
                    else -> Screen.Exit
                }
        )
        EXIT_APP -> state.copy(screen = Screen.Exit)
        // Allows to re-start the application
        ON_EXIT_APP -> state.copy(screen = Screen.Login)
        else -> state
    }
}

val babyReducer = Reducer<AppState> { state, action ->
    when (action) {
        is BABY_ADDED -> state.copy(babies = state.babies + action.baby)
        is BABY_MODIFIED -> {
            val index = state.babies.indexOfFirst { it.key == action.baby.key }
            val updatedBabies = state.babies.toMutableList().apply {
                removeAt(index)
                add(index, action.baby)
            }
            state.copy(babies = updatedBabies)
        }
        is BABY_REMOVED -> state.copy(babies = state.babies - action.baby)
        is SELECT_BABY -> state.copy(selectedBaby = action.baby)
        UNSELECT_BABY -> state.copy(selectedBaby = null)
        else -> state
    }
}

val timelineReducer = Reducer<AppState> { state, action ->
    when (action) {
        is ENTRY_ADDED -> state.copy(timeline = state.timeline + action.entry)
        is ENTRY_MODIFIED -> {
            val index = state.timeline.entries.indexOfFirst { it.remoteId == action.entry.remoteId }
            val updatedTimeline = state.timeline.entries.toMutableList().apply {
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
        DISCONNECTED -> INITIAL_STATE
        FETCH_BABIES -> state.copy(babies = emptyList())
        FETCH_TIMELINE -> state.copy(timeline = Timeline())
        else -> state
    }
}