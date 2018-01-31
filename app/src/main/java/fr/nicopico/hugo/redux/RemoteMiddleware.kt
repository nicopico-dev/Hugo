@file:Suppress("ClassName")

package fr.nicopico.hugo.redux

import fr.nicopico.hugo.service.AuthService
import fr.nicopico.hugo.service.RemoteService
import fr.nicopico.hugo.utils.Result
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import redux.INIT
import redux.api.Dispatcher
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
                    val result = async { remoteService.fetchTimeline(store) }.await()
                    val resultAction: Any = when(result) {
                        is Result.Success -> REMOTE_DATA_FETCHED(result.value)
                        is Result.Failure -> REMOTE_DATA_ERROR(result.error)
                    }
                    store.dispatch(resultAction)
                }
            }
            is ADD_ENTRY -> async { remoteService.saveEntry(action.entry) }
            is UPDATE_ENTRY -> async { remoteService.saveEntry(action.newEntry) }
            is REMOVE_ENTRY -> async{ remoteService.removeEntry(action.entry) }
        }
        return next.dispatch(action)
    }
}
