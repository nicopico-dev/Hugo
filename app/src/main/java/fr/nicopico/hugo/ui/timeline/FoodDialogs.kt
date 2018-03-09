package fr.nicopico.hugo.ui.timeline

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.*
import fr.nicopico.hugo.model.CareType.FOOD
import fr.nicopico.hugo.redux.ADD_ENTRY
import fr.nicopico.hugo.redux.ReduxView
import fr.nicopico.hugo.redux.UPDATE_ENTRY
import fr.nicopico.hugo.ui.shared.*
import fr.nicopico.hugo.ui.timeline.EditTimelineEntryDialogTrait.Companion.ARG_ENTRY_KEY
import kotlinx.android.synthetic.main.dialog_add_food.*
import kotlinx.android.synthetic.main.dialog_form.*
import kotlinx.android.synthetic.main.view_bottle_feeding.view.*
import kotlinx.android.synthetic.main.view_breast_extraction.view.*
import kotlinx.android.synthetic.main.view_breast_feeding.view.*
import kotlinx.coroutines.experimental.Deferred
import java.util.*

fun Fragment.addFoodDialog() = AddFoodDialogFragment.create()
        .show(fragmentManager!!, null)
fun Fragment.editFoodDialog(entry: Timeline.Entry): Unit = EditFoodDialogFragment.create(entry)
        .show(fragmentManager!!, null)

open class AddFoodDialogFragment : TimelineEntryDialogFragment(), ReduxView {

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

        val context = context!!
        entry.cares
                .map {
                    // The `when` is cast to Any without an explicit cast... (kotlin 1.2.21)
                    @Suppress("USELESS_CAST")
                    when (it) {
                        is BreastFeeding -> BreastFeedingView(context).bindTo(it) as FoodView<Care>
                        is BottleFeeding -> BottleFeedingView(context).bindTo(it)
                        is BreastExtraction -> BreastExtractionView(context).bindTo(it)
                        else -> throw UnsupportedOperationException("Care $it")
                    }
                }
                .forEach {
                    addFoodView(it)
                }
    }

    override fun onSubmit(view: View) {
        val updatedEntry = buildEntry()
        dispatch(UPDATE_ENTRY(updatedEntry))
        dismiss()
    }
}

interface FoodView<out T : Care> {
    fun retrieve(): T
}

private class BottleFeedingView(
        context: Context,
        val content: String? = null
) : ConstraintLayout(context), FoodView<BottleFeeding> {

    constructor(context: Context): this(context, null)

    private lateinit var _content: String

    init {
        inflate(context, R.layout.view_bottle_feeding, this)
        if (content != null) {
            updateBottleContent(content)
        }
    }

    fun bindTo(care: BottleFeeding): BottleFeedingView {
        edtBottle.textS = care.volume.toString()
        updateBottleContent(care.content)
        return this
    }

    override fun retrieve(): BottleFeeding {
        val volume = edtBottle.asInt()
        return BottleFeeding(volume = volume, content = _content)
    }

    private fun updateBottleContent(content: String) {
        _content = content
        txtBottleContent.textI = when (content) {
            BottleFeeding.MATERNAL_MILK -> R.string.maternal_milk
            BottleFeeding.ARTIFICIAL_MILK -> R.string.artificial_milk
            else -> TODO("not implemented")
        }
    }
}

private class BreastFeedingView(context: Context) : ConstraintLayout(context), FoodView<BreastFeeding> {

    init {
        inflate(context, R.layout.view_breast_feeding, this)
    }

    fun bindTo(care: BreastFeeding): BreastFeedingView {
        edtLeftBreastDuration.textS = care.leftDuration?.toString()
        edtRightBreastDuration.textS = care.rightDuration?.toString()
        return this
    }

    override fun retrieve(): BreastFeeding {
        return BreastFeeding(
                leftDuration = edtLeftBreastDuration.asIntOrNull(),
                rightDuration = edtRightBreastDuration.asIntOrNull()
        )
    }
}

private class BreastExtractionView(context: Context) : ConstraintLayout(context), FoodView<BreastExtraction> {

    init {
        inflate(context, R.layout.view_breast_extraction, this)
    }

    fun bindTo(care: BreastExtraction): BreastExtractionView {
        edtExtraction.textS = care.volume.toString()
        chkLeftBreast.isChecked = Breast.LEFT in care.breasts
        chkRightBreast.isChecked = Breast.RIGHT in care.breasts
        return this
    }

    override fun retrieve(): BreastExtraction {
        val volume = edtExtraction.asInt()
        val leftBreast = chkLeftBreast.isChecked
        val rightBreast = chkRightBreast.isChecked
        val breasts = when {
            leftBreast && rightBreast -> EnumSet.allOf(Breast::class.java)
            leftBreast -> EnumSet.of(Breast.LEFT)
            rightBreast -> EnumSet.of(Breast.RIGHT)
            else -> EnumSet.noneOf(Breast::class.java)
        }
        return BreastExtraction(volume = volume, breasts = breasts)
    }
}

private fun TextView.asInt(): Int = asIntOrNull()!!
private fun TextView.asIntOrNull(): Int? = when {
    text.isNullOrEmpty() -> null
    else -> Integer.parseInt(text.toString())
}