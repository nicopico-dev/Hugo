package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.StateProvider
import fr.nicopico.hugo.android.ui.shared.SpaceItemDecoration
import fr.nicopico.hugo.android.ui.timeline.entry.TimelineEntryDialogFragment
import fr.nicopico.hugo.android.utils.children
import fr.nicopico.hugo.android.utils.click
import fr.nicopico.hugo.android.utils.dimensionForOffset
import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.model.BottleFeeding
import fr.nicopico.hugo.domain.model.BreastExtraction
import fr.nicopico.hugo.domain.model.BreastFeeding
import fr.nicopico.hugo.domain.model.CareType.FOOD
import fr.nicopico.hugo.domain.model.Diversification
import fr.nicopico.hugo.domain.model.FoodType
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.model.allFoodTypes
import fr.nicopico.hugo.domain.redux.ADD_ENTRY
import kotlinx.android.synthetic.main.dialog_add_food.*

open class AddFoodDialogFragment : TimelineEntryDialogFragment(), StateProvider {

    companion object {
        fun create() = AddFoodDialogFragment()
    }

    override val dialogTitleId = R.string.care_type_food
    override val formLayoutId: Int? = R.layout.dialog_add_food
    override val dateOrTimeTextView: TextView
        get() = txtDateOrTime
    override val state: AppState
        get() = _state

    private val foodChoiceAdapter by lazy {
        FoodChoiceAdapter(context!!).apply {
            click = { addFoodView(it) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context!!

        rcvFoodChoice.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        rcvFoodChoice.adapter = foodChoiceAdapter
        SpaceItemDecoration(rcvFoodChoice.layoutManager, dimensionForOffset(R.dimen.space_micro))
                .also { rcvFoodChoice.addItemDecoration(it) }

        // TODO Use state to render only food types enabled for selectedBaby
        foodChoiceAdapter.submitList(allFoodTypes)
    }

    override fun buildEntry(): Timeline.Entry {
        val cares = foodContainer.children.map { (it as FoodView).retrieve() }
        return Timeline.Entry(FOOD, entryTime, cares)
    }

    override fun onSubmit(view: View) {
        val entry = buildEntry()
        dispatch(ADD_ENTRY(entry))
        dismiss()
    }

    protected fun addFoodView(foodType: FoodType): FoodView {
        val context = context!!

        val foodView: FoodView = when (foodType) {
            BreastFeeding::class -> BreastFeedingView(context)
            BreastExtraction::class -> BreastExtractionView(context)
            BottleFeeding.Maternal::class -> BottleFeedingView(context, BottleFeeding.CONTENT_MATERNAL_MILK)
            BottleFeeding.Artificial::class -> BottleFeedingView(context, BottleFeeding.CONTENT_ARTIFICIAL_MILK)
            Diversification::class -> DiversificationView(context)
            else -> throw UnsupportedOperationException("Unsupported FoodType $foodType")
        }

        foodContainer.addView(foodView as View)
        foodChoiceAdapter.remove(foodType)

        // Remove button behavior
        foodView.findViewById<View>(R.id.imgRemove).click {
            foodContainer.removeView(foodView)
            foodChoiceAdapter.add(foodType)
        }

        return foodView
    }
}
