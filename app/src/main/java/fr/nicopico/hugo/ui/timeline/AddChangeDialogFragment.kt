package fr.nicopico.hugo.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.domain.model.*
import fr.nicopico.hugo.domain.redux.ADD_ENTRY
import fr.nicopico.hugo.domain.redux.appStore
import fr.nicopico.hugo.ui.shared.click
import fr.nicopico.hugo.ui.shared.confirm
import kotlinx.android.synthetic.main.dialog_add_change.*

class AddChangeDialogFragment : AddTimelineEntryDialogFragment() {

    companion object {
        fun create() = AddChangeDialogFragment()
    }

    override val dateOrTimeTextView: TextView
        get() = txtDateOrTime

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val themedInflater = LayoutInflater.from(context)
        return themedInflater.inflate(R.layout.dialog_add_change, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSubmit.click {
            val time = getEntryTime()
            val cares = mutableListOf<Care>()
            if (chkPee.isChecked) cares.add(Pee)
            if (chkPoo.isChecked) cares.add(Poo)

            if (cares.isNotEmpty()) {
                val entry = Timeline.Entry(CareType.CHANGE, time, cares)
                appStore.dispatch(ADD_ENTRY(entry))
                dismiss()
            } else {
                // Nothing selected -> ask for confirmation
                confirm(context!!, R.string.confirm_no_change_message, R.string.care_change_nothing, R.string.cancel) {
                    val entry = Timeline.Entry(CareType.CHANGE, time, cares)
                    appStore.dispatch(ADD_ENTRY(entry))
                    dismiss()
                }
            }
        }
    }

}