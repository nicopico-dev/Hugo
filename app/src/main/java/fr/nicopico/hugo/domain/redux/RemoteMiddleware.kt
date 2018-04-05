package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.android.HugoLogger
import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.model.Baby
import fr.nicopico.hugo.domain.model.Message
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.services.BabyService
import fr.nicopico.hugo.domain.services.Fetcher
import fr.nicopico.hugo.domain.services.TimelineService

import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

class RemoteMiddleware(
        private val babyService: BabyService,
        private val timelineService: TimelineService
) : Middleware<AppState>, HugoLogger {

    @Suppress("ComplexMethod")
    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        if (action is AUTHENTICATED) {
            babyService.user = action.user
            timelineService.user = action.user
        } else if (action is SELECT_BABY) {
            timelineService.baby = action.baby
        }

        when(action) {
            FETCH_BABIES -> babyService.fetch(ReduxBabyFetcher(store))
            STOP_FETCHING_BABIES -> babyService.stopFetching()
            is ADD_BABY -> babyService.addEntry(action.baby)
            is UPDATE_BABY -> babyService.updateEntry(action.baby)
            is REMOVE_BABY -> babyService.removeEntry(action.baby)

            FETCH_TIMELINE -> timelineService.fetch(ReduxTimelineFetcher(store))
            STOP_FETCHING_TIMELINE -> timelineService.stopFetching()
            is ADD_ENTRY -> timelineService.addEntry(action.entry)
            is UPDATE_ENTRY -> timelineService.updateEntry(action.entry)
            is REMOVE_ENTRY -> timelineService.removeEntry(action.entry)

            is REMOTE_ERROR -> store.dispatch(DISPLAY_MESSAGE(Message.error("RemoteError: ${action.error.message}")))
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