package fr.nicopico.hugo.ui.timeline

import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.domain.model.*
import fr.nicopico.hugo.ui.shared.dimensionForOffset
import kotlin.properties.Delegates

class TimelineAdapter(
        context: Context,
        data: List<Timeline.Entry> = emptyList()
) : RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    var data by Delegates.observable(data) { _, previous, new ->
        val callback = TimelineEntryDiffUtilCallback(previous, new)
        val diffResult = DiffUtil.calculateDiff(callback, false)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = inflater.inflate(R.layout.item_timeline, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val timeFormat = DateFormat.getTimeFormat(itemView.context)
        private val txtType: TextView = itemView.findViewById(R.id.txtType)
        private val txtTime: TextView = itemView.findViewById(R.id.txtTime)
        private val details: ViewGroup = itemView.findViewById(R.id.details)

        fun bind(entry: Timeline.Entry) {
            txtType.setText(when (entry.type) {
                CareType.CHANGE -> R.string.care_type_change
                CareType.FOOD -> R.string.care_type_food
                CareType.HEALTH_HYGIENE -> R.string.care_type_health_hygiene
            })

            txtTime.text = timeFormat.format(entry.time)

            details.removeAllViews()
            entry.cares.forEach {
                details.addView(careView(it))
            }
        }

        private fun careView(care: Care): View {
            return when(care) {
                UmbilicalCord -> textView(R.string.care_umbilical_cord)
                Face -> textView(R.string.care_face)
                Bath -> textView(R.string.care_bath)
                Vitamins -> textView(R.string.care_vitamins)
                Pee -> textView(R.string.care_change_pee)
                Poo -> textView(R.string.care_change_poo)
                is BreastFeeding -> textView(
                        when(care.breast) {
                            Breast.LEFT -> R.string.care_breast_feeding_left
                            Breast.RIGHT -> R.string.care_breast_feeding_right
                        },
                        care.duration
                )
                is BreastExtraction -> textView(R.string.care_breast_extraction, care.volume)
                is BottleFeeding -> textView(
                        when {
                            care.maternalMilk -> R.string.care_bottle_feeding_maternal
                            else -> R.string.care_bottle_feeding_artificial
                        },
                        care.volume
                )
            }
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

    class TimelineEntryDiffUtilCallback(
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