package fr.nicopico.hugo.ui.timeline

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.*
import fr.nicopico.hugo.redux.ADD_ENTRY
import fr.nicopico.hugo.redux.appStore
import fr.nicopico.hugo.ui.shared.click
import fr.nicopico.hugo.ui.shared.hide
import fr.nicopico.hugo.ui.shared.show
import kotlinx.android.synthetic.main.dialog_add_food.*
import java.util.*

class AddFoodDialogFragment : AddTimelineEntryDialogFragment() {

    companion object {
        fun create() = AddFoodDialogFragment()
    }

    private var bottleMaternalFeedingView: View? = null
    private var bottleArtificialFeedingView: View? = null
    private var breastFeedingView: View? = null
    private var breastExtractionView: View? = null

    private val inflater by lazy { LayoutInflater.from(context) }

    override val dateOrTimeTextView: TextView
        get() = txtDateOrTime

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val themedInflater = LayoutInflater.from(context)
        return themedInflater.inflate(R.layout.dialog_add_food, container, false)
    }

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

        btnSubmit.click {
            val time = getEntryTime()
            val cares = listOfNotNull(
                    getMaternalBottleFeedingCare(),
                    getArtificialBottleFeedingCare(),
                    getBreastFeedingCare(Breast.LEFT),
                    getBreastFeedingCare(Breast.RIGHT),
                    getBreastExtraction()
            )

            val entry = Timeline.Entry(CareType.FOOD, time, cares)
            appStore.dispatch(ADD_ENTRY(entry))

            dismiss()
        }
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
            BottleFeeding(Integer.parseInt(volumeText), true)
        }
    }

    private fun getArtificialBottleFeedingCare(): Care? {
        return bottleArtificialFeedingView?.let {
            val volumeText = it.findViewById<EditText>(R.id.edtBottle).text.toString()
            BottleFeeding(Integer.parseInt(volumeText), false)
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
            val volumeText = it.findViewById<EditText>(R.id.edtBottle).text.toString()
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