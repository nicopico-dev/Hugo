package fr.nicopico.hugo.ui.timeline

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.*
import fr.nicopico.hugo.redux.ADD_ENTRY
import fr.nicopico.hugo.redux.ReduxView
import fr.nicopico.hugo.redux.appStore
import kotlinx.android.synthetic.main.dialog_add_health_and_hygiene.*

fun Fragment.addHealthAndHygieneDialog() = AddHealthAndHygieneDialogFragment.create().show(fragmentManager!!, null)
fun Fragment.editHealthAndHygieneDialog(entry: Timeline.Entry): Unit = TODO()

class AddHealthAndHygieneDialogFragment : TimelineEntryDialogFragment(), ReduxView {

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
        TODO("buildEntry is not implemented")
    }

    override fun onSubmit(view: View) {
        val cares = mutableListOf<Care>()
        if (chkBath.isChecked) cares.add(Bath)
        if (chkFace.isChecked) cares.add(Face)
        if (chkUmbilicalCord.isChecked) cares.add(UmbilicalCord)
        if (chkVitamins.isChecked) cares.add(Vitamins)

        val entry = Timeline.Entry(CareType.HEALTH_HYGIENE, entryTime, cares)
        appStore.dispatch(ADD_ENTRY(entry))

        dismiss()
    }
}