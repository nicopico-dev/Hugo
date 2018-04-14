package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.model.Screen
import fr.nicopico.hugo.domain.model.Timeline
import redux.api.Reducer

private fun AppState.withDefaultScreen(): AppState {
    val defaultScreen: Screen = when {
        user == null -> Screen.Loading
        // Go directly to the timeline if a baby is selected
        selectedBaby != null -> Screen.Timeline
        else -> Screen.BabySelection
    }

    return if (screen == defaultScreen) this
    else return copy(screen = defaultScreen)
}

val navigationReducer = Reducer<AppState> { state, action ->
    when (action) {
        is AUTHENTICATED -> state.withDefaultScreen()
        is SELECT_BABY -> state.copy(screen = Screen.Timeline, selectedBaby = action.baby)
        UNSELECT_BABY -> state.copy(screen = Screen.BabySelection, selectedBaby = null)
        EXIT_APP -> state.copy(screen = Screen.Exit)
        // Allow returning to the application
        ON_APP_EXIT -> state.withDefaultScreen()
        else -> state
    }
}

val goBackReducer = Reducer<AppState> { state, action ->
    if (action != GO_BACK) state
    else when (state.screen) {
        Screen.Timeline -> navigationReducer.reduce(state, UNSELECT_BABY)
        else -> navigationReducer.reduce(state, EXIT_APP)
    }
}

val messageReducer = Reducer<AppState> { state, action ->
    when (action) {
        is DISPLAY_MESSAGE -> state.copy(message = action.message)
        action is REMOVE_MESSAGE && state.message == action.message -> state.copy(message = null)
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
        else -> state
    }
}

val timelineReducer = Reducer<AppState> { state, action ->
    when (action) {
        is ENTRY_ADDED -> state.copy(timeline = state.timeline + action.entry)
        is ENTRY_MODIFIED -> state.copy(timeline = state.timeline.replace(action.entry))
        is ENTRY_REMOVED -> state.copy(timeline = state.timeline - action.entry)
        else -> state
    }
}

fun createRemoteReducer(initialState: AppState) =
        Reducer<AppState> { state, action ->
            when (action) {
                is AUTHENTICATED -> state.copy(user = action.user)
                DISCONNECTED -> initialState
                FETCH_BABIES -> state.copy(babies = emptyList())
                FETCH_TIMELINE -> state.copy(timeline = Timeline())
                else -> state
            }
        }