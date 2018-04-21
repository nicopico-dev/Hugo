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

    private val pendingAddedEntries = mutableListOf<Timeline.Entry>()
    private val pendingRemovedEntries = mutableListOf<Timeline.Entry>()
    private var dispatchJob: Job? = null

    @Suppress("RedundantUnitExpression")
    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        when (action) {
            is ENTRY_ADDED -> pendingAddedEntries.add(action.entry)
            is ENTRY_MODIFIED -> {
                pendingRemovedEntries.add(action.entry)
                pendingAddedEntries.add(action.entry)
            }
            is ENTRY_REMOVED -> pendingRemovedEntries.add(action.entry)
            else -> return next.dispatch(action)
        }

        debug { "$action added to the batch actions, reset timer" }
        // TODO Add loading state

        val addedEntries = pendingAddedEntries.toList()
        val removedEntries = pendingRemovedEntries.toList()

        dispatchJob?.cancel()
        dispatchJob = launch(Background) {
            delay(delayMs)
            launch(UI) {
                dispatchJob = null
                debug("Send batch actions")
                store.dispatch(ENTRIES_REMOVED(removedEntries))
                pendingRemovedEntries.removeAll(removedEntries)
                store.dispatch(ENTRIES_ADDED(addedEntries))
                pendingAddedEntries.removeAll(addedEntries)
            }
        }

        return Unit
    }
}