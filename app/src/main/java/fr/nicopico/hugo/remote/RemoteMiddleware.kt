@file:Suppress("ClassName")

package fr.nicopico.hugo.remote

import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.redux.ADD_ENTRY
import fr.nicopico.hugo.redux.AppState
import fr.nicopico.hugo.redux.REMOVE_ENTRY
import fr.nicopico.hugo.redux.UPDATE_ENTRY
import fr.nicopico.hugo.utils.Result
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import redux.INIT
import redux.api.Dispatcher
import redux.api.Reducer
import redux.api.Store
import redux.api.enhancer.Middleware

class RemoteMiddleware(
        private val authService: AuthService,
        private val remoteService: RemoteService
): Middleware<AppState> {

    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        when (action) {
            INIT -> authService.addOnUserChangeListener { user ->
                store.dispatch(REQUEST_REMOTE_DATA(user))
            }
            is REQUEST_REMOTE_DATA -> {
                launch {
                    val result = async { remoteService.fetch() }.await()
                    val resultAction: Any = when(result) {
                        is Result.Success -> REMOTE_DATA_FETCHED(result.value)
                        is Result.Failure -> REMOTE_DATA_ERROR(result.error)
                    }
                    store.dispatch(resultAction)
                }
            }
            is ADD_ENTRY -> async { remoteService.addEntry(action.entry) }
            is UPDATE_ENTRY -> async { remoteService.replaceEntry(action.oldEntry, action.newEntry) }
            is REMOVE_ENTRY -> async{ remoteService.removeEntry(action.entry) }
        }
        return next.dispatch(action)
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

data class REQUEST_REMOTE_DATA(val user: User)
data class REMOTE_DATA_FETCHED(val timeline: List<Timeline.Entry>)
data class REMOTE_DATA_ERROR(val error: Exception)