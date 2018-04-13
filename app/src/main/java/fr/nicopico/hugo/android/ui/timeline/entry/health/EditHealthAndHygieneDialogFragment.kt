package fr.nicopico.hugo.android.ui.timeline.entry.health

import android.os.Bundle
import android.view.View
import fr.nicopico.hugo.android.then
import fr.nicopico.hugo.android.ui.timeline.entry.EditTimelineEntryDialogTrait
import fr.nicopico.hugo.android.utils.argument
import fr.nicopico.hugo.android.utils.withArguments
import fr.nicopico.hugo.domain.model.Bath
import fr.nicopico.hugo.domain.model.Face
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.model.UmbilicalCord
import fr.nicopico.hugo.domain.model.Vitamins
import fr.nicopico.hugo.domain.redux.UPDATE_ENTRY
import kotlinx.android.synthetic.main.dialog_add_health_and_hygiene.*
import kotlinx.android.synthetic.main.dialog_form.*
import kotlinx.coroutines.experimental.Deferred

class EditHealthAndHygieneDialogFragment : AddHealthAndHygieneDialogFragment(), EditTimelineEntryDialogTrait {

    companion object {
        fun create(entry: Timeline.Entry) = EditHealthAndHygieneDialogFragment()
                .withArguments(EditTimelineEntryDialogTrait.ARG_ENTRY_KEY to entry.remoteId)
    }

    override val entryKey: String by argument(EditTimelineEntryDialogTrait.ARG_ENTRY_KEY)
    override var deferredEntry: Deferred<Timeline.Entry>? = null
    override val deleteView: View
        get() = imgDelete

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AddHealthAndHygieneDialogFragment>.onCreate(savedInstanceState)
        super<EditTimelineEntryDialogTrait>.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super<AddHealthAndHygieneDialogFragment>.onViewCreated(view, savedInstanceState)
        super<EditTimelineEntryDialogTrait>.onViewCreated(view, savedInstanceState)

        deferredEntry?.then {
            entryTime = it.time
            chkBath.isChecked = Bath in it.cares
            chkFace.isChecked = Face in it.cares
            chkUmbilicalCord.isChecked = UmbilicalCord in it.cares
            chkVitamins.isChecked = Vitamins in it.cares
        }
    }

    override fun buildEntry(): Timeline.Entry {
        return super.buildEntry().copy(remoteId = entryKey)
    }

    override fun displayEntry(entry: Timeline.Entry) {
        entryTime = entry.time
        chkBath.isChecked = Bath in entry.cares
        chkFace.isChecked = Face in entry.cares
        chkUmbilicalCord.isChecked = UmbilicalCord in entry.cares
        chkVitamins.isChecked = Vitamins in entry.cares
    }

    override fun onSubmit(view: View) {
        val updatedEntry = buildEntry()
        dispatch(UPDATE_ENTRY(updatedEntry))
        dismiss()
    }
}