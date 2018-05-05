package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.os.Bundle
import android.view.View
import fr.nicopico.hugo.android.ui.timeline.entry.EditTimelineEntryDialogTrait
import fr.nicopico.hugo.android.utils.argument
import fr.nicopico.hugo.android.utils.withArguments
import fr.nicopico.hugo.domain.model.BottleFeeding
import fr.nicopico.hugo.domain.model.BreastExtraction
import fr.nicopico.hugo.domain.model.BreastFeeding
import fr.nicopico.hugo.domain.model.Diversification
import fr.nicopico.hugo.domain.model.FoodCare
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.redux.UPDATE_ENTRY
import kotlinx.android.synthetic.main.dialog_form.*
import kotlinx.coroutines.experimental.Deferred

class EditFoodDialogFragment : AddFoodDialogFragment(), EditTimelineEntryDialogTrait {

    companion object {
        fun create(entry: Timeline.Entry) = EditFoodDialogFragment()
                .withArguments(EditTimelineEntryDialogTrait.ARG_ENTRY_KEY to entry.remoteId)
    }

    override val entryKey by argument<String>(EditTimelineEntryDialogTrait.ARG_ENTRY_KEY)
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
                        is BreastFeeding -> BreastFeedingView(context).bindTo(it) as FoodView<FoodCare>
                        is BottleFeeding -> BottleFeedingView(context).bindTo(it)
                        is BreastExtraction -> BreastExtractionView(context).bindTo(it)
                        is Diversification -> DiversificationView(context).bindTo(it)
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