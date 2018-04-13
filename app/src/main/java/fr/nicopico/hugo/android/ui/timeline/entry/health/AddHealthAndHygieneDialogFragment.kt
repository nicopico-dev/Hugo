package fr.nicopico.hugo.android.ui.timeline.entry.health

import android.os.Bundle
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.ui.timeline.entry.TimelineEntryDialogFragment
import fr.nicopico.hugo.domain.model.Bath
import fr.nicopico.hugo.domain.model.Care
import fr.nicopico.hugo.domain.model.CareType
import fr.nicopico.hugo.domain.model.Face
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.model.UmbilicalCord
import fr.nicopico.hugo.domain.model.Vitamins
import fr.nicopico.hugo.domain.redux.ADD_ENTRY
import kotlinx.android.synthetic.main.dialog_add_health_and_hygiene.*

open class AddHealthAndHygieneDialogFragment : TimelineEntryDialogFragment() {

    companion object {
        fun create() = AddHealthAndHygieneDialogFragment()
    }

    override val dialogTitleId = R.string.care_type_health_hygiene
    override val formLayoutId: Int? = R.layout.dialog_add_health_and_hygiene
    override val dateOrTimeTextView: TextView
        get() = txtDateOrTime

    private val checkboxes by lazy {
        arrayOf(chkBath, chkFace, chkUmbilicalCord, chkVitamins)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enable the submit button if at least one checkbox is checked
        val checkedChangeListener = { _: View?, _: Boolean? ->
            submittable = checkboxes.any { it.isChecked }
        }
        for (checkbox in checkboxes) {
            checkbox.setOnCheckedChangeListener(checkedChangeListener)
        }
        // Initial state
        checkedChangeListener.invoke(null, null)
    }

    override fun buildEntry(): Timeline.Entry {
        val cares = mutableListOf<Care>()
        if (chkBath.isChecked) cares.add(Bath)
        if (chkFace.isChecked) cares.add(Face)
        if (chkUmbilicalCord.isChecked) cares.add(UmbilicalCord)
        if (chkVitamins.isChecked) cares.add(Vitamins)

        return Timeline.Entry(CareType.HEALTH_HYGIENE, entryTime, cares)
    }

    override fun onSubmit(view: View) {
        val entry = buildEntry()
        dispatch(ADD_ENTRY(entry))
        dismiss()
    }
}