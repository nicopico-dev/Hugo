@file:Suppress("ClassName")

package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.service.AuthService
import fr.nicopico.hugo.service.RemoteService
import fr.nicopico.hugo.service.TimelineFetcher
import kotlinx.coroutines.experimental.async
import redux.INIT
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

class RemoteMiddleware(
        private val authService: AuthService,
        private val remoteService: RemoteService
) : Middleware<AppState> {

    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        when (action) {
            INIT -> authService.apply {
                addOnUserChangeListener { store.dispatch(REQUEST_REMOTE_DATA(it)) }
                async { signIn() }
            }
            is REQUEST_REMOTE_DATA -> remoteService.fetchTimeline(ReduxTimelineFetcher(store))
            is ADD_ENTRY -> remoteService.addEntry(action.entry)
            is UPDATE_ENTRY -> remoteService.updateEntry(action.entry)
            is REMOVE_ENTRY -> remoteService.removeEntry(action.entry)
        }
        return next.dispatch(action)
    }

    private class ReduxTimelineFetcher(val store: Store<AppState>) : TimelineFetcher {
        override fun onEntryAdded(entry: Timeline.Entry) {
            store.dispatch(ENTRY_ADDED(entry))
        }

        override fun onEntryModified(entry: Timeline.Entry) {
            store.dispatch(ENTRY_MODIFIED(entry))
        }

        override fun onEntryRemoved(entry: Timeline.Entry) {
            store.dispatch(ENTRY_REMOVED(entry))
        }

        override fun onError(exception: Exception) {
            store.dispatch(REMOTE_DATA_ERROR(exception))
        }
    }
}
