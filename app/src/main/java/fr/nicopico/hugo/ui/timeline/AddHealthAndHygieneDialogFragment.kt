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
import kotlinx.android.synthetic.main.dialog_add_health_and_hygiene.*

class AddHealthAndHygieneDialogFragment : AddTimelineEntryDialogFragment() {

    companion object {
        fun create() = AddHealthAndHygieneDialogFragment()
    }

    override val dateOrTimeTextView: TextView
        get() = txtDateOrTime

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val themedInflater = LayoutInflater.from(context)
        return themedInflater.inflate(R.layout.dialog_add_health_and_hygiene, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSubmit.click {
            val time = getEntryTime()
            val cares = mutableListOf<Care>()
            if (chkBath.isChecked) cares.add(Bath)
            if (chkFace.isChecked) cares.add(Face)
            if (chkUmbilicalCord.isChecked) cares.add(UmbilicalCord)
            if (chkVitamins.isChecked) cares.add(Vitamins)

            val entry = Timeline.Entry(CareType.HEALTH_HYGIENE, time, cares)
            appStore.dispatch(ADD_ENTRY(entry))

            dismiss()
        }
    }

}