package fr.nicopico.hugo.ui.timeline

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.*
import fr.nicopico.hugo.redux.ADD_ENTRY
import fr.nicopico.hugo.redux.REMOVE_ENTRY
import fr.nicopico.hugo.redux.ReduxView
import fr.nicopico.hugo.redux.UPDATE_ENTRY
import fr.nicopico.hugo.service.timelineService
import fr.nicopico.hugo.ui.shared.*
import fr.nicopico.hugo.utils.loadSuspend
import fr.nicopico.hugo.utils.then
import kotlinx.android.synthetic.main.dialog_add_change.*
import kotlinx.android.synthetic.main.dialog_form.*
import kotlinx.coroutines.experimental.Deferred

fun Fragment.addChangeDialog() = AddChangeDialogFragment.create().show(fragmentManager!!, null)
fun Fragment.editChangeDialog(entry: Timeline.Entry) = EditChangeDialogFragment.create(entry).show(fragmentManager!!, null)

open class AddChangeDialogFragment : TimelineEntryDialogFragment(), ReduxView {

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

class EditChangeDialogFragment : AddChangeDialogFragment() {

    companion object {
        private const val ARG_ENTRY_KEY = "ARG_ENTRY_KEY"

        fun create(entry: Timeline.Entry) = EditChangeDialogFragment()
                .withArguments(ARG_ENTRY_KEY to entry.remoteId)
    }

    private val entryKey by argument<String>(ARG_ENTRY_KEY)
    private var deferredEntry: Deferred<Timeline.Entry>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            deferredEntry = loadSuspend {
                timelineService.get(entryKey)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgDelete.apply {
            show()
            click {
                dispatch(REMOVE_ENTRY(buildEntry()))
                dismiss()
            }
        }

        deferredEntry?.then {
            entryTime = it.time
            chkPee.isChecked = Pee in it.cares
            chkPoo.isChecked = Poo in it.cares
        }
    }

    override fun buildEntry(): Timeline.Entry {
        return super.buildEntry().copy(remoteId = entryKey)
    }

    override fun onSubmit(view: View) {
        val updatedEntry = buildEntry()
        checkAndDispatch(updatedEntry, UPDATE_ENTRY(updatedEntry))
    }
}