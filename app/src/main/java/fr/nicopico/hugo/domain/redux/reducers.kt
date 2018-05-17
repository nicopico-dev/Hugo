package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.model.Screen
import fr.nicopico.hugo.domain.model.ScreenStack
import fr.nicopico.hugo.domain.model.Timeline
import redux.api.Reducer

val navigationReducer = Reducer<AppState> { state, action ->
    when (action) {
        is AUTHENTICATED,
        // Allows coming back to app after ON_EXIT
        is ON_APP_EXIT -> state.copy(screenStack = ScreenStack(state.defaultScreen))
        is EXIT_APP -> state.copy(screenStack = ScreenStack(Screen.Exit))

        is PUSH_SCREEN -> state.copy(screenStack = state.screenStack.push(action.screen))
        is POP_SCREEN -> state.copy(screenStack = state.screenStack.pop())
        is POP_SCREEN_UNTIL -> state.copy(screenStack = state.screenStack.popUntil(action.screen))
        else -> state
    }.let {
        // Un-select baby when going back to the selection screen
        if (it.screen == Screen.BabySelection && it.selectedBaby != null) {
            it.copy(selectedBaby = null)
        } else {
            it
        }
    }
}

private val AppState.defaultScreen: List<Screen>
    get() = when {
        user == null -> listOf(Screen.Loading)
        selectedBaby == null -> listOf(Screen.BabySelection)
        else -> listOf(Screen.BabySelection, Screen.Timeline)
    }

val messageReducer = Reducer<AppState> { state, action ->
    when (action) {
        is DISPLAY_MESSAGE -> state.copy(message = action.message)
        action is REMOVE_MESSAGE && state.message == action.message -> state.copy(message = null)
        is START_LOADING -> state.copy(loading = true)
        is FINISHED_LOADING -> state.copy(loading = false)
        else -> state
    }
}

val babyReducer = Reducer<AppState> { state, action ->
    when (action) {
        is SELECT_BABY -> state.copy(selectedBaby = action.baby)
        is UPDATE_BABY -> if (action.baby.key == state.selectedBaby?.key) {
            // Update selected baby immediately
            state.copy(selectedBaby = action.baby)
        } else {
            state
        }
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
        is ENTRIES_SET -> state.copy(timeline = Timeline(action.entries))
        else -> state
    }
}

fun createRemoteReducer(initialState: AppState) =
        Reducer<AppState> { state, action ->
            when (action) {
                is AUTHENTICATED -> state.copy(user = action.user)
                is DISCONNECTED -> initialState
                is FETCH_BABIES -> state.copy(babies = emptyList())
                is FETCH_TIMELINE -> state.copy(timeline = Timeline())
                else -> state
            }
        }