package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.os.Bundle
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.ui.timeline.entry.TimelineEntryDialogFragment
import fr.nicopico.hugo.android.utils.children
import fr.nicopico.hugo.android.utils.click
import fr.nicopico.hugo.android.utils.hide
import fr.nicopico.hugo.android.utils.show
import fr.nicopico.hugo.domain.model.BottleFeeding
import fr.nicopico.hugo.domain.model.Care
import fr.nicopico.hugo.domain.model.CareType.FOOD
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.redux.ADD_ENTRY
import kotlinx.android.synthetic.main.dialog_add_food.*

open class AddFoodDialogFragment : TimelineEntryDialogFragment() {

    companion object {
        fun create() = AddFoodDialogFragment()
    }

    override val dialogTitleId = R.string.care_type_food
    override val formLayoutId: Int? = R.layout.dialog_add_food
    override val dateOrTimeTextView: TextView
        get() = txtDateOrTime

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context!!
        btnAddMaternalBottleFeeding.click { addFoodView(BottleFeedingView(context, BottleFeeding.MATERNAL_MILK)) }
        btnAddArtificialBottleFeeding.click { addFoodView(BottleFeedingView(context, BottleFeeding.ARTIFICIAL_MILK)) }
        btnAddBreastFeeding.click { addFoodView(BreastFeedingView(context)) }
        btnAddBreastExtraction.click { addFoodView(BreastExtractionView(context)) }
    }

    override fun buildEntry(): Timeline.Entry {
        val cares = foodContainer.children.map { (it as FoodView<Care>).retrieve() }
        return Timeline.Entry(FOOD, entryTime, cares)
    }

    override fun onSubmit(view: View) {
        val entry = buildEntry()
        dispatch(ADD_ENTRY(entry))
        dismiss()
    }

    protected fun addFoodView(foodView: FoodView<Care>) {
        val toggleView = when(foodView) {
            is BreastFeedingView -> btnAddBreastFeeding
            is BreastExtractionView -> btnAddBreastExtraction
            is BottleFeedingView -> when(foodView.content) {
                BottleFeeding.MATERNAL_MILK -> btnAddMaternalBottleFeeding
                BottleFeeding.ARTIFICIAL_MILK -> btnAddArtificialBottleFeeding
                else -> null
            }
            else -> null
        }

        foodContainer.addView(foodView as View)
        toggleView?.hide()

        // Remove button behavior
        foodView.findViewById<View>(R.id.imgRemove).click {
            foodContainer.removeView(foodView)
            toggleView?.show()
        }
    }
}

