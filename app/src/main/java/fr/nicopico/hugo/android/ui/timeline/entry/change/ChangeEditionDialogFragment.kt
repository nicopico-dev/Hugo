package fr.nicopico.hugo.android.ui.timeline.entry.change

import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.ui.timeline.entry.BaseEntryEditionDialogFragment
import fr.nicopico.hugo.domain.model.Care
import fr.nicopico.hugo.domain.model.CareType
import fr.nicopico.hugo.domain.model.Pee
import fr.nicopico.hugo.domain.model.Poo
import fr.nicopico.hugo.domain.model.Screen
import fr.nicopico.hugo.domain.model.Timeline
import kotlinx.android.synthetic.main.dialog_add_change.*
import java.util.*

class ChangeEditionDialogFragment : BaseEntryEditionDialogFragment() {

    override val dialogTitleId = R.string.care_type_change
    override val formLayoutId: Int? = R.layout.dialog_add_change
    override val dateOrTimeTextView: TextView
        get() = txtDateOrTime

    override fun getStateEntry(): Timeline.Entry?
            = (state.screen as? Screen.TimelineEntryEdition)?.entry

    override fun buildUpdatedEntry(entry: Timeline.Entry?, entryTime: Date): Timeline.Entry {
        val cares = mutableListOf<Care>()
        if (chkPee.isChecked) cares.add(Pee)
        if (chkPoo.isChecked) cares.add(Poo)

        return Timeline.Entry(entry?.remoteId, CareType.CHANGE, entryTime, cares)
    }

    override fun checkEntryValidity(entry: Timeline.Entry): ValidityResult {
        return if (entry.cares.isEmpty()) {
            ValidityResult.ConfirmationNeeded(
                    message = R.string.confirm_no_change_message,
                    confirmButtonLabel = R.string.care_change_nothing
            )
        } else {
            return ValidityResult.Valid
        }
    }

    override fun render(entry: Timeline.Entry) {
        chkPee.isChecked = Pee in entry.cares
        chkPoo.isChecked = Poo in entry.cares
    }

}