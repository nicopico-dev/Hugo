package fr.nicopico.hugo.ui.timeline

import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.*
import fr.nicopico.hugo.redux.ADD_ENTRY
import fr.nicopico.hugo.redux.StateHelper
import fr.nicopico.hugo.ui.shared.confirm
import kotlinx.android.synthetic.main.dialog_add_change.*

class AddChangeDialogFragment : AddTimelineEntryDialogFragment(), StateHelper {

    companion object {
        fun create() = AddChangeDialogFragment()
    }

    override val dialogTitleId = R.string.care_type_change
    override val formLayoutId: Int? = R.layout.dialog_add_change
    override val dateOrTimeTextView: TextView
        get() = txtDateOrTime

    override fun onSubmit(view: View) {
        val time = getEntryTime()
        val cares = mutableListOf<Care>()
        if (chkPee.isChecked) cares.add(Pee)
        if (chkPoo.isChecked) cares.add(Poo)

        if (cares.isNotEmpty()) {
            val entry = Timeline.Entry(CareType.CHANGE, time, cares)
            dispatch(ADD_ENTRY(entry))
            dismiss()
        } else {
            // Nothing selected -> ask for confirmation
            confirm(context!!, R.string.confirm_no_change_message, R.string.care_change_nothing, R.string.cancel) {
                val entry = Timeline.Entry(CareType.CHANGE, time, cares)
                dispatch(ADD_ENTRY(entry))
                dismiss()
            }
        }
    }
}