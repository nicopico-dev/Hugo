package fr.nicopico.hugo.ui.timeline

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.view.View
import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.redux.REMOVE_ENTRY
import fr.nicopico.hugo.redux.ReduxView
import fr.nicopico.hugo.service.timelineService
import fr.nicopico.hugo.ui.shared.click
import fr.nicopico.hugo.ui.shared.show
import fr.nicopico.hugo.utils.loadSuspend
import kotlinx.coroutines.experimental.Deferred

interface EditTimelineEntryDialogTrait : LifecycleOwner, ReduxView {

    companion object {
        const val ARG_ENTRY_KEY = "ARG_ENTRY_KEY"
    }

    val entryKey: String
    var deferredEntry: Deferred<Timeline.Entry>?
    val imgDelete: View

    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            deferredEntry = loadSuspend {
                timelineService.get(entryKey)
            }
        }
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imgDelete.apply {
            show()
            click {
                dispatch(REMOVE_ENTRY(buildEntry()))
                dismiss()
            }
        }
    }

    fun buildEntry(): Timeline.Entry
    fun dismiss()
}