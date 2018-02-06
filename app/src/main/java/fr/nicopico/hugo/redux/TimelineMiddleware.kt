package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.service.Fetcher
import fr.nicopico.hugo.service.TimelineService
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.warn
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

class TimelineMiddleware(private val timelineService: TimelineService) : Middleware<AppState>, HugoLogger {

    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        if (action is AUTHENTICATED) {
            timelineService.user = action.user
        } else if (action is SELECT_BABY) {
            timelineService.baby = action.baby
        }

        // Do not process actions until the user is authenticated
        if (timelineService.user != null && timelineService.baby != null) {
            when(action) {
                FETCH_TIMELINE -> timelineService.fetch(ReduxTimelineFetcher(store))
                STOP_FETCHING_TIMELINE -> timelineService.stopFetching()
                is ADD_ENTRY -> timelineService.addEntry(action.entry)
                is UPDATE_ENTRY -> timelineService.updateEntry(action.entry)
                is REMOVE_ENTRY -> timelineService.removeEntry(action.entry)
            }
        } else {
            warn { "Ignore $action as timelineService is not ready" }
        }
        return next.dispatch(action)
    }

    private class ReduxTimelineFetcher(val store: Store<AppState>) : Fetcher<Timeline.Entry> {
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
            store.dispatch(REMOTE_ERROR(exception))
        }
    }
}