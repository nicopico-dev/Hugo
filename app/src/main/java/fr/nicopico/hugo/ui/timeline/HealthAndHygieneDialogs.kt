package fr.nicopico.hugo.ui.timeline

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.*
import fr.nicopico.hugo.redux.ADD_ENTRY
import fr.nicopico.hugo.redux.ReduxView
import fr.nicopico.hugo.redux.UPDATE_ENTRY
import fr.nicopico.hugo.ui.shared.argument
import fr.nicopico.hugo.ui.shared.withArguments
import fr.nicopico.hugo.ui.timeline.EditTimelineEntryDialogTrait.Companion.ARG_ENTRY_KEY
import fr.nicopico.hugo.utils.then
import kotlinx.android.synthetic.main.dialog_add_health_and_hygiene.*
import kotlinx.android.synthetic.main.dialog_form.*
import kotlinx.coroutines.experimental.Deferred

fun Fragment.addHealthAndHygieneDialog() = AddHealthAndHygieneDialogFragment.create().show(fragmentManager!!, null)
fun Fragment.editHealthAndHygieneDialog(entry: Timeline.Entry): Unit = EditHealthAndHygieneDialogFragment.create(entry).show(fragmentManager!!, null)

open class AddHealthAndHygieneDialogFragment : TimelineEntryDialogFragment(), ReduxView {

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

class EditHealthAndHygieneDialogFragment : AddHealthAndHygieneDialogFragment(), EditTimelineEntryDialogTrait {

    companion object {
        fun create(entry: Timeline.Entry) = EditHealthAndHygieneDialogFragment()
                .withArguments(ARG_ENTRY_KEY to entry.remoteId)
    }

    override val entryKey: String by argument(ARG_ENTRY_KEY)
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