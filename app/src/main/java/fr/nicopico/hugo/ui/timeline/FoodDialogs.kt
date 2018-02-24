package fr.nicopico.hugo.ui.timeline

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.*
import fr.nicopico.hugo.redux.ADD_ENTRY
import fr.nicopico.hugo.redux.ReduxView
import fr.nicopico.hugo.redux.UPDATE_ENTRY
import fr.nicopico.hugo.ui.shared.*
import fr.nicopico.hugo.ui.timeline.EditTimelineEntryDialogTrait.Companion.ARG_ENTRY_KEY
import kotlinx.android.synthetic.main.dialog_add_food.*
import kotlinx.android.synthetic.main.dialog_form.*
import kotlinx.coroutines.experimental.Deferred
import java.util.*

fun Fragment.addFoodDialog() = AddFoodDialogFragment.create().show(fragmentManager!!, null)
fun Fragment.editFoodDialog(entry: Timeline.Entry): Unit = EditFoodDialogFragment.create(entry).show(fragmentManager!!, null)

open class AddFoodDialogFragment : TimelineEntryDialogFragment(), ReduxView {

    companion object {
        fun create() = AddFoodDialogFragment()
    }

    private var bottleMaternalFeedingView: View? = null
    private var bottleArtificialFeedingView: View? = null
    private var breastFeedingView: View? = null
    private var breastExtractionView: View? = null

    private val inflater by lazy { LayoutInflater.from(context) }

    override val dialogTitleId = R.string.care_type_food
    override val formLayoutId: Int? = R.layout.dialog_add_food
    override val dateOrTimeTextView: TextView
        get() = txtDateOrTime


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO Animate layout changes
        addMaternalBottleFeeding.click {
            it.toggle(R.layout.include_maternal_bottle_feeding) { v -> bottleMaternalFeedingView = v }
        }
        addArtificialBottleFeeding.click {
            it.toggle(R.layout.include_artificial_bottle_feeding) { v -> bottleArtificialFeedingView = v }
        }
        addBreastFeeding.click {
            it.toggle(R.layout.include_breast_feeding) { v -> breastFeedingView = v }
        }
        addBreastExtraction.click {
            it.toggle(R.layout.include_breast_extraction) { v -> breastExtractionView = v }
        }
    }

    override fun buildEntry(): Timeline.Entry {
        val cares = listOfNotNull(
                getMaternalBottleFeedingCare(),
                getArtificialBottleFeedingCare(),
                getBreastFeedingCare(Breast.LEFT),
                getBreastFeedingCare(Breast.RIGHT),
                getBreastExtraction()
        )

        return Timeline.Entry(CareType.FOOD, entryTime, cares)
    }

    override fun onSubmit(view: View) {
        val entry = buildEntry()
        dispatch(ADD_ENTRY(entry))
        dismiss()
    }

    private fun View.toggle(@LayoutRes layout: Int, propertySetter: (View) -> Unit) {
        this.hide()
        val detailsView = inflater.inflate(layout, foodContainer, false) as ViewGroup
        foodContainer.addView(detailsView)
        propertySetter.invoke(detailsView)

        // Remove button behavior
        detailsView.findViewById<View>(R.id.imgRemove).click {
            foodContainer.removeView(detailsView)
            this.show()
        }
    }

    private fun getMaternalBottleFeedingCare(): Care? {
        return bottleMaternalFeedingView?.let {
            val volumeText = it.findViewById<EditText>(R.id.edtBottle).text.toString()
            BottleFeeding(Integer.parseInt(volumeText), BottleFeeding.MATERNAL_MILK)
        }
    }

    private fun getArtificialBottleFeedingCare(): Care? {
        return bottleArtificialFeedingView?.let {
            val volumeText = it.findViewById<EditText>(R.id.edtBottle).text.toString()
            BottleFeeding(Integer.parseInt(volumeText), BottleFeeding.ARTIFICIAL_MILK)
        }
    }

    private fun getBreastFeedingCare(breast: Breast): Care? {
        val editId = when(breast) {
            Breast.LEFT -> R.id.edtLeftBreastDuration
            Breast.RIGHT -> R.id.edtRightBreastDuration
        }
        return breastFeedingView?.let {
            val volumeText = it.findViewById<EditText>(editId).text.toString()
            BreastFeeding(breast, Integer.parseInt(volumeText))
        }
    }

    private fun getBreastExtraction(): Care? {
        return breastExtractionView?.let {
            val volumeText = it.findViewById<EditText>(R.id.edtExtraction).text.toString()
            val leftBreast = it.findViewById<CheckBox>(R.id.chkLeftBreast).isChecked
            val rightBreast = it.findViewById<CheckBox>(R.id.chkRightBreast).isChecked
            val breasts = when {
                leftBreast && rightBreast -> EnumSet.allOf(Breast::class.java)
                leftBreast -> EnumSet.of(Breast.LEFT)
                rightBreast -> EnumSet.of(Breast.RIGHT)
                else -> EnumSet.noneOf(Breast::class.java)
            }

            BreastExtraction(Integer.parseInt(volumeText), breasts)
        }
    }
}

class EditFoodDialogFragment : AddFoodDialogFragment(), EditTimelineEntryDialogTrait {

    companion object {
        fun create(entry: Timeline.Entry) = EditFoodDialogFragment()
                .withArguments(ARG_ENTRY_KEY to entry.remoteId)
    }

    override val entryKey by argument<String>(ARG_ENTRY_KEY)
    override var deferredEntry: Deferred<Timeline.Entry>? = null
    override val deleteView: View
        get() = imgDelete

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AddFoodDialogFragment>.onCreate(savedInstanceState)
        super<EditTimelineEntryDialogTrait>.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super<AddFoodDialogFragment>.onViewCreated(view, savedInstanceState)
        super<EditTimelineEntryDialogTrait>.onViewCreated(view, savedInstanceState)
    }

    override fun buildEntry(): Timeline.Entry {
        return super.buildEntry().copy(remoteId = entryKey)
    }

    override fun displayEntry(entry: Timeline.Entry) {
        entryTime = entry.time
        // TODO
    }

    override fun onSubmit(view: View) {
        val updatedEntry = buildEntry()
        UPDATE_ENTRY(updatedEntry)
    }
}