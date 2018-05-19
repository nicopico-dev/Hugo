package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.ReduxView
import fr.nicopico.hugo.android.ui.ReduxLifecycleListener
import fr.nicopico.hugo.android.ui.shared.SpaceItemDecoration
import fr.nicopico.hugo.android.ui.timeline.entry.BaseEntryEditionDialogFragment
import fr.nicopico.hugo.android.utils.children
import fr.nicopico.hugo.android.utils.click
import fr.nicopico.hugo.android.utils.dimensionForOffset
import fr.nicopico.hugo.android.utils.visible
import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.model.BottleFeeding
import fr.nicopico.hugo.domain.model.BreastExtraction
import fr.nicopico.hugo.domain.model.BreastFeeding
import fr.nicopico.hugo.domain.model.Care
import fr.nicopico.hugo.domain.model.CareType
import fr.nicopico.hugo.domain.model.Diversification
import fr.nicopico.hugo.domain.model.FoodCare
import fr.nicopico.hugo.domain.model.FoodType
import fr.nicopico.hugo.domain.model.FoodTypes
import fr.nicopico.hugo.domain.model.Screen
import fr.nicopico.hugo.domain.model.Timeline
import kotlinx.android.synthetic.main.dialog_add_food.*
import kotlinx.android.synthetic.main.dialog_form.*
import java.util.*

class FoodEditionDialogFragment : BaseEntryEditionDialogFragment(), ReduxView {

    override val dialogTitleId = R.string.care_type_food
    override val formLayoutId: Int? = R.layout.dialog_add_food
    override val dateOrTimeTextView: TextView
        get() = txtDateOrTime

    private val foodChoiceAdapter by lazy { FoodChoiceAdapter(context!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ReduxLifecycleListener
                .create(this)
                .restrictOn { it.selectedBaby?.disabledFoodTypes }
                .observe()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context!!
        with(rcvFoodChoice) {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = foodChoiceAdapter
            addItemDecoration(SpaceItemDecoration(layoutManager, dimensionForOffset(R.dimen.space_micro)))
        }

        foodChoiceAdapter.itemClick { addFoodView(it) }

        imgSettings.apply {
            visible = true
            click { FoodSettingsDialogFragment.show(fragmentManager!!) }
        }
    }

    override fun subscribe(subscriber: () -> Unit) = _subscribe(subscriber)

    override fun render(state: AppState) {
        val disabledFoodTypes = state.selectedBaby?.disabledFoodTypes ?: emptySet()
        val allowedFoodTypes = FoodTypes.allTypes - disabledFoodTypes
        foodChoiceAdapter.submitList(allowedFoodTypes)
    }

    override fun getStateEntry(): Timeline.Entry? = (state.screen as? Screen.TimelineEntryEdition)?.entry

    override fun buildUpdatedEntry(entry: Timeline.Entry?, entryTime: Date): Timeline.Entry {
        val cares = foodContainer.children.map {
            (it as FoodView).retrieve()
        }
        return Timeline.Entry(entry?.remoteId, CareType.FOOD, entryTime, cares)
    }

    override fun checkEntryValidity(entry: Timeline.Entry): ValidityResult {
        return when {
            entry.cares.isEmpty() -> ValidityResult.Invalid(R.string.care_food_invalid_selection)
            entry.cares.all(::validateFoodCare) -> ValidityResult.Valid
            else -> ValidityResult.Invalid(R.string.care_food_invalid_input)
        }
    }

    override fun render(entry: Timeline.Entry) {
        for (care in entry.cares) {
            val foodCare = care as FoodCare
            addFoodView(foodCare.foodType).bindTo(care)
        }
    }

    private fun addFoodView(foodType: FoodType): FoodView {
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

    private fun validateFoodCare(care: Care): Boolean {
        return when (care) {
            is BreastFeeding -> arrayOf(care.leftDuration, care.rightDuration).any { it != null }
            is BreastExtraction -> care.volume > 0
            is BottleFeeding.Maternal,
            is BottleFeeding.Artificial,
            is BottleFeeding.Other -> with(care as BottleFeeding) { volume > 0 && content.isNotBlank() }
            is Diversification -> care.quantity > 0 && care.aliment.isNotBlank()
            else -> throw IllegalStateException("$care is not valid for FoodEdition")
        }
    }

}