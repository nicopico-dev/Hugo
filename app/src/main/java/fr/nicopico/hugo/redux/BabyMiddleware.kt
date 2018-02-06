package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.Baby
import fr.nicopico.hugo.service.BabyService
import fr.nicopico.hugo.service.Fetcher
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

class BabyMiddleware(private val babyService: BabyService) : Middleware<AppState> {

    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        when(action) {
            is AUTHENTICATED -> babyService.user = action.user

            FETCH_BABIES -> babyService.fetch(ReduxBabyFetcher(store))
            STOP_FETCHING_BABIES -> babyService.stopFetching()
            is ADD_BABY -> babyService.addEntry(action.baby)
            is UPDATE_BABY -> babyService.updateEntry(action.baby)
            is REMOVE_BABY -> babyService.removeEntry(action.baby)
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