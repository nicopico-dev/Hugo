package fr.nicopico.hugo.android.ui.timeline.entry.health

import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.ui.timeline.entry.BaseEntryEditionDialogFragment
import fr.nicopico.hugo.domain.model.Bath
import fr.nicopico.hugo.domain.model.Care
import fr.nicopico.hugo.domain.model.CareType
import fr.nicopico.hugo.domain.model.Face
import fr.nicopico.hugo.domain.model.Screen
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.model.UmbilicalCord
import fr.nicopico.hugo.domain.model.Vitamins
import kotlinx.android.synthetic.main.dialog_add_health_and_hygiene.*
import java.util.*

class HealthEditionDialogFragment : BaseEntryEditionDialogFragment() {

    override val dialogTitleId = R.string.care_type_health_hygiene
    override val formLayoutId: Int? = R.layout.dialog_add_health_and_hygiene
    override val dateOrTimeTextView: TextView
        get() = txtDateOrTime

    private val checkboxes by lazy {
        arrayOf(chkBath, chkFace, chkUmbilicalCord, chkVitamins)
    }

    override fun getStateEntry(): Timeline.Entry?
            = (state.screen as? Screen.TimelineEntryEdition)?.entry

    override fun buildUpdatedEntry(entry: Timeline.Entry?, entryTime: Date): Timeline.Entry {
        val cares = mutableListOf<Care>()
        if (chkBath.isChecked) cares.add(Bath)
        if (chkFace.isChecked) cares.add(Face)
        if (chkUmbilicalCord.isChecked) cares.add(UmbilicalCord)
        if (chkVitamins.isChecked) cares.add(Vitamins)

        return Timeline.Entry(entry?.remoteId, CareType.HEALTH_HYGIENE, entryTime, cares)
    }

    override fun checkEntryValidity(entry: Timeline.Entry): ValidityResult {
        return if (checkboxes.any { it.isChecked }) {
            ValidityResult.Valid
        } else {
            ValidityResult.Invalid(R.string.care_hygiene_invalid_selection)
        }
    }

    override fun render(entry: Timeline.Entry) {
        chkBath.isChecked = Bath in entry.cares
        chkFace.isChecked = Face in entry.cares
        chkUmbilicalCord.isChecked = UmbilicalCord in entry.cares
        chkVitamins.isChecked = Vitamins in entry.cares
    }
}