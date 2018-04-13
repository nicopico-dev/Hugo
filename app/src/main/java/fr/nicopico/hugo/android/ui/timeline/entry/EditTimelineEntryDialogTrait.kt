package fr.nicopico.hugo.android.ui.timeline.entry

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.view.View
import fr.nicopico.hugo.android.ReduxDispatcher
import fr.nicopico.hugo.android.loadSuspend
import fr.nicopico.hugo.android.then
import fr.nicopico.hugo.android.utils.click
import fr.nicopico.hugo.android.utils.show
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.redux.REMOVE_ENTRY
import fr.nicopico.hugo.domain.services.TimelineService
import kotlinx.coroutines.experimental.Deferred

interface EditTimelineEntryDialogTrait : LifecycleOwner, ReduxDispatcher {

    companion object {
        const val ARG_ENTRY_KEY = "ARG_ENTRY_KEY"
    }

    val timelineService: TimelineService

    val entryKey: String
    var deferredEntry: Deferred<Timeline.Entry>?
    val deleteView: View

    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            deferredEntry = loadSuspend {
                timelineService.get(entryKey)
            }
        }
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        deleteView.apply {
            show()
            click {
                dispatch(REMOVE_ENTRY(buildEntry()))
                dismiss()
            }
        }

        deferredEntry?.then { displayEntry(it) }
    }

    fun buildEntry(): Timeline.Entry
    fun displayEntry(entry: Timeline.Entry)
    fun dismiss()
}