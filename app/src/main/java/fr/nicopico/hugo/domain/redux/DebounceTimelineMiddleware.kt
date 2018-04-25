package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.android.Background
import fr.nicopico.hugo.android.HugoLogger
import fr.nicopico.hugo.android.debug
import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.model.Timeline
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

class DebounceTimelineMiddleware(
        private val delayMs: Long
) : Middleware<AppState>, HugoLogger {

    companion object {
        private const val LOADING_ENTRIES = "timeline.entries"
    }

    private var pendingEntries: List<Timeline.Entry>? = null
    private var dispatchJob: Job? = null

    @Suppress("RedundantUnitExpression")
    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        val timelineAction: TimelineAction = when (action) {
            is ENTRY_ADDED -> { entries: Entries -> entries + action.entry }
            is ENTRY_MODIFIED -> { entries: Entries ->
                entries.map {
                    if (it.remoteId == action.entry.remoteId) {
                        action.entry
                    } else {
                        it
                    }
                }
            }
            is ENTRY_REMOVED -> { entries: Entries -> entries - action.entry }
            else -> return next.dispatch(action)
        }

        debug { "$action added to the batch actions, reset timer" }
        if (!store.state.loading) {
            store.dispatch(START_LOADING(LOADING_ENTRIES))
        }

        pendingEntries = timelineAction(pendingEntries ?: store.state.timeline.asEntryList())

        dispatchJob?.cancel()
        dispatchJob = launch(Background) {
            delay(delayMs)
            launch(UI) {
                debug("Send batch actions")
                store.dispatch(ENTRIES_SET(pendingEntries!!))
                store.dispatch(FINISHED_LOADING(LOADING_ENTRIES))
                dispatchJob = null
                pendingEntries = null
            }
        }

        return Unit
    }
}

private typealias Entries = List<Timeline.Entry>
private typealias TimelineAction = (List<Timeline.Entry>) -> List<Timeline.Entry>