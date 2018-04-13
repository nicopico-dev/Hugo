package fr.nicopico.hugo.android.ui.timeline.entry.change

import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.ui.timeline.entry.TimelineEntryDialogFragment
import fr.nicopico.hugo.android.utils.confirm
import fr.nicopico.hugo.domain.model.Care
import fr.nicopico.hugo.domain.model.CareType
import fr.nicopico.hugo.domain.model.Pee
import fr.nicopico.hugo.domain.model.Poo
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.redux.ADD_ENTRY
import kotlinx.android.synthetic.main.dialog_add_change.*

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