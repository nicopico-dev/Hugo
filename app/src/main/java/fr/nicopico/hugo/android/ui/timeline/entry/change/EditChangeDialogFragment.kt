package fr.nicopico.hugo.android.ui.timeline.entry.change

import android.os.Bundle
import android.view.View
import fr.nicopico.hugo.android.ui.timeline.entry.EditTimelineEntryDialogTrait
import fr.nicopico.hugo.android.utils.argument
import fr.nicopico.hugo.android.utils.withArguments
import fr.nicopico.hugo.domain.model.Pee
import fr.nicopico.hugo.domain.model.Poo
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.redux.UPDATE_ENTRY
import kotlinx.android.synthetic.main.dialog_add_change.*
import kotlinx.android.synthetic.main.dialog_form.*
import kotlinx.coroutines.experimental.Deferred

class EditChangeDialogFragment : AddChangeDialogFragment(), EditTimelineEntryDialogTrait {

    companion object {
        fun create(entry: Timeline.Entry) = EditChangeDialogFragment()
                .withArguments(EditTimelineEntryDialogTrait.ARG_ENTRY_KEY to entry.remoteId)
    }

    override val entryKey by argument<String>(EditTimelineEntryDialogTrait.ARG_ENTRY_KEY)
    override var deferredEntry: Deferred<Timeline.Entry>? = null
    override val deleteView: View
        get() = imgDelete

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AddChangeDialogFragment>.onCreate(savedInstanceState)
        super<EditTimelineEntryDialogTrait>.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super<AddChangeDialogFragment>.onViewCreated(view, savedInstanceState)
        super<EditTimelineEntryDialogTrait>.onViewCreated(view, savedInstanceState)
    }

    override fun buildEntry(): Timeline.Entry {
        return super.buildEntry().copy(remoteId = entryKey)
    }

    override fun displayEntry(entry: Timeline.Entry) {
        entryTime = entry.time
        chkPee.isChecked = Pee in entry.cares
        chkPoo.isChecked = Poo in entry.cares
    }

    override fun onSubmit(view: View) {
        val updatedEntry = buildEntry()
        checkAndDispatch(updatedEntry, UPDATE_ENTRY(updatedEntry))
    }
}