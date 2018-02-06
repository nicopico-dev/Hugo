package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.Baby
import fr.nicopico.hugo.service.BabyService
import fr.nicopico.hugo.service.Fetcher
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.warn
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

class BabyMiddleware(private val babyService: BabyService) : Middleware<AppState>, HugoLogger {

    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        if (action is AUTHENTICATED) {
            babyService.user = action.user
        }

        // Do not process actions until the user is authenticated
        if (babyService.user != null) {
            when (action) {
                FETCH_BABIES -> babyService.fetch(ReduxBabyFetcher(store))
                STOP_FETCHING_BABIES -> babyService.stopFetching()
                is ADD_BABY -> babyService.addEntry(action.baby)
                is UPDATE_BABY -> babyService.updateEntry(action.baby)
                is REMOVE_BABY -> babyService.removeEntry(action.baby)
            }
        } else {
            warn { "Ignore $action as babyService is not ready" }
        }
        return next.dispatch(action)
    }

    private class ReduxBabyFetcher(val store: Store<AppState>) : Fetcher<Baby> {
        override fun onEntryAdded(entry: Baby) {
            store.dispatch(BABY_ADDED(entry))
        }

        override fun onEntryModified(entry: Baby) {
            store.dispatch(BABY_MODIFIED(entry))
        }

        override fun onEntryRemoved(entry: Baby) {
            store.dispatch(BABY_REMOVED(entry))
        }

        override fun onError(exception: Exception) {
            store.dispatch(REMOTE_ERROR(exception))
        }
    }
}