package fr.nicopico.hugo.android.ui.timeline

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.ui.timeline.EditTimelineEntryDialogTrait.Companion.ARG_ENTRY_KEY
import fr.nicopico.hugo.android.utils.argument
import fr.nicopico.hugo.android.utils.confirm
import fr.nicopico.hugo.android.utils.withArguments
import fr.nicopico.hugo.domain.model.Care
import fr.nicopico.hugo.domain.model.CareType
import fr.nicopico.hugo.domain.model.Pee
import fr.nicopico.hugo.domain.model.Poo
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.redux.ADD_ENTRY
import fr.nicopico.hugo.domain.redux.UPDATE_ENTRY
import kotlinx.android.synthetic.main.dialog_add_change.*
import kotlinx.android.synthetic.main.dialog_form.*
import kotlinx.coroutines.experimental.Deferred

fun Fragment.addChangeDialog() = AddChangeDialogFragment.create()
        .show(fragmentManager!!, null)
fun Fragment.editChangeDialog(entry: Timeline.Entry) = EditChangeDialogFragment.create(entry)
        .show(fragmentManager!!, null)

open class AddChangeDialogFragment : TimelineEntryDialogFragment() {

    companion object {
        fun create() = AddChangeDialogFragment()
    }

    override val dialogTitleId = R.string.care_type_change
    override val formLayoutId: Int? = R.layout.dialog_add_change
    override val dateOrTimeTextView: TextView
        get() = txtDateOrTime

    override fun buildEntry(): Timeline.Entry {
        val cares = mutableListOf<Care>()
        if (chkPee.isChecked) cares.add(Pee)
        if (chkPoo.isChecked) cares.add(Poo)

        return Timeline.Entry(CareType.CHANGE, entryTime, cares)
    }

    override fun onSubmit(view: View) {
        val entry = buildEntry()
        checkAndDispatch(entry, ADD_ENTRY(entry))
    }

    protected fun checkAndDispatch(entry: Timeline.Entry, action: Any) {
        if (entry.cares.isNotEmpty()) {
            dispatch(action)
            dismiss()
        } else {
            confirm(context!!,
                    R.string.confirm_no_change_message,
                    R.string.care_change_nothing,
                    R.string.cancel,
                    {
                        dispatch(action)
                        dismiss()
                    }
            )
        }
    }
}

class EditChangeDialogFragment : AddChangeDialogFragment(), EditTimelineEntryDialogTrait {

    companion object {
        fun create(entry: Timeline.Entry) = EditChangeDialogFragment()
                .withArguments(ARG_ENTRY_KEY to entry.remoteId)
    }

    override val entryKey by argument<String>(ARG_ENTRY_KEY)
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