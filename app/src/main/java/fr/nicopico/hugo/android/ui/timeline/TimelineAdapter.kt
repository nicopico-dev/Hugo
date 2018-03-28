package fr.nicopico.hugo.android.ui.timeline

import android.content.Context
import android.graphics.Typeface
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.utils.dimensionForOffset
import fr.nicopico.hugo.android.utils.longClick
import fr.nicopico.hugo.domain.model.Bath
import fr.nicopico.hugo.domain.model.BottleFeeding
import fr.nicopico.hugo.domain.model.BreastExtraction
import fr.nicopico.hugo.domain.model.BreastFeeding
import fr.nicopico.hugo.domain.model.Care
import fr.nicopico.hugo.domain.model.CareType
import fr.nicopico.hugo.domain.model.Face
import fr.nicopico.hugo.domain.model.Pee
import fr.nicopico.hugo.domain.model.Poo
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.model.UmbilicalCord
import fr.nicopico.hugo.domain.model.Vitamins
import fr.nicopico.hugo.domain.utils.getTimeFormat
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_timeline.*
import kotlin.properties.Delegates

typealias TimelineListener = (Timeline.Entry) -> Unit

class TimelineAdapter(
        context: Context,
        data: List<Timeline.Entry> = emptyList()
) : RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    var data by Delegates.observable(data) { _, old, new ->
        val callback = TimelineEntryDiffUtilCallback(old, new)
        val diffResult = DiffUtil.calculateDiff(callback, false)
        diffResult.dispatchUpdatesTo(this)
    }

    var longClickListener: TimelineListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = inflater.inflate(R.layout.item_timeline, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(
            override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val timeFormat = getTimeFormat(itemView.context)
        private lateinit var entry: Timeline.Entry

        init {
            itemView.longClick { longClickListener?.invoke(entry) }
        }

        fun bind(entry: Timeline.Entry) {
            this.entry = entry

            txtType.setText(when (entry.type) {
                CareType.CHANGE -> R.string.care_type_change
                CareType.FOOD -> R.string.care_type_food
                CareType.HEALTH_HYGIENE -> R.string.care_type_health_hygiene
            })

            txtTime.text = timeFormat.format(entry.time)

            // Clean-up for recycled views
            details.removeAllViews()

            // Special case for Change without pee nor poo
            if (entry.type == CareType.CHANGE && entry.cares.isEmpty()) {
                val txtNothing = TextView(itemView.context).apply {
                    setText(R.string.care_change_nothing)
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                    setTypeface(typeface, Typeface.ITALIC)
                }
                details.addView(txtNothing)
            } else {
                entry.cares
                        .map { createCareView(it) }
                        .forEach { details.addView(it) }
            }
        }

        private fun createCareView(care: Care): View {
            return when (care) {
                UmbilicalCord -> textView(R.string.care_umbilical_cord)
                Face -> textView(R.string.care_face)
                Bath -> textView(R.string.care_bath)
                Vitamins -> textView(R.string.care_vitamins)
                Pee -> textView(R.string.care_change_pee)
                Poo -> textView(R.string.care_change_poo)
                is BreastFeeding -> createBreastFeedingView(care)
                is BreastExtraction -> textView(R.string.care_breast_extraction, care.volume)
                is BottleFeeding -> createBottleFeedingView(care)
            }
        }

        private fun createBreastFeedingView(care: BreastFeeding): TextView {
            return textView(
                    if (care.leftDuration != null && care.rightDuration != null) R.string.care_breast_feeding_both
                    else if (care.leftDuration != null) R.string.care_breast_feeding_left
                    else if (care.rightDuration != null) R.string.care_breast_feeding_right
                    else throw IllegalStateException("Breast feeding without any duration"),
                    care.leftDuration, care.rightDuration
            )
        }

        private fun createBottleFeedingView(care: BottleFeeding): TextView {
            return textView(
                    when (care.content) {
                        BottleFeeding.MATERNAL_MILK -> R.string.care_bottle_feeding_maternal
                        BottleFeeding.ARTIFICIAL_MILK -> R.string.care_bottle_feeding_artificial
                        BottleFeeding.WATER -> R.string.care_bottle_feeding_water
                        else -> R.string.care_bottle_feeding_other
                    },
                    care.volume
            )
        }

        private fun textView(@StringRes text: Int, vararg params: Any?, icon: Int? = null): TextView {
            val context = itemView.context
            return TextView(context).apply {
                if (params.isEmpty()) {
                    this.setText(text)
                } else {
                    this.text = context.getString(text, *params)
                }

                val leftIcon = icon ?: R.drawable.bullet
                setCompoundDrawablesWithIntrinsicBounds(leftIcon, 0, 0, 0)
                compoundDrawablePadding = context.dimensionForOffset(R.dimen.space_micro)
            }
        }
    }

    private class TimelineEntryDiffUtilCallback(
            private val previousEntries: List<Timeline.Entry>,
            private val newEntries: List<Timeline.Entry>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = previousEntries.size

        override fun getNewListSize(): Int = newEntries.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val previous = previousEntries[oldItemPosition]
            val new = newEntries[newItemPosition]
            return previous.type == new.type
                    && previous.time == new.time
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return previousEntries[oldItemPosition] == newEntries[newItemPosition]
        }
    }
}