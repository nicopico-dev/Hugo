package fr.nicopico.hugo.android.ui.timeline

import android.content.Context
import android.graphics.Typeface
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.utils.dimensionForOffset
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
import fr.nicopico.hugo.domain.utils.getDateFormat
import fr.nicopico.hugo.domain.utils.getTimeFormat
import kotlinx.android.synthetic.main.item_timeline_entry.*
import kotlinx.android.synthetic.main.item_timeline_section.*
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

typealias TimelineListener = (Timeline.Entry) -> Unit

class TimelineAdapter(private val context: Context) : GroupAdapter<ViewHolder>() {

    var longClickListener: TimelineListener? = null
    var data: List<Timeline.Section> by Delegates.observable(emptyList(), ::onDataChanged)

    private val rootSection = Section(emptyList())

    init {
        setOnItemLongClickListener { item, _ ->
            (item as? EntryItem)
                    ?.let { longClickListener?.invoke(it.entry); true }
                    ?: false
        }
        add(rootSection)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onDataChanged(prop: KProperty<*>, oldData: List<Timeline.Section>, newDate: List<Timeline.Section>) {
        rootSection.update(
                newDate.map {
                    Section(
                            SectionHeader(context, it),
                            it.entries.map { entry ->
                                EntryItem(context, entry)
                            }
                    )
                }
        )
    }
}

private class SectionHeader(
        private val context: Context,
        val section: Timeline.Section
) : Item() {
    private val dateFormat = getDateFormat(context)

    override fun getLayout() = R.layout.item_timeline_section

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.txtSection.text = dateFormat.format(section.time)
        viewHolder.txtTotalMilk.text = context.getString(R.string.section_total_milk, section.totalMilk)
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        return other is SectionHeader
                && section.time == other.section.time
    }

    override fun hashCode(): Int {
        return section.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is SectionHeader
                && section == other.section
    }
}

private class EntryItem(
        private val context: Context,
        val entry: Timeline.Entry
) : Item() {
    private val timeFormat = getTimeFormat(context)

    override fun getLayout() = R.layout.item_timeline_entry

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.txtType.setText(when (entry.type) {
            CareType.CHANGE -> R.string.care_type_change
            CareType.FOOD -> R.string.care_type_food
            CareType.HEALTH_HYGIENE -> R.string.care_type_health_hygiene
        })

        viewHolder.txtTime.text = timeFormat.format(entry.time)

        // Clean-up for recycled views
        val details = viewHolder.details.apply { removeAllViews() }

        // Special case for Change without pee nor poo
        if (entry.type == CareType.CHANGE && entry.cares.isEmpty()) {
            val txtNothing = TextView(context).apply {
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

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        return other is EntryItem
                && entry.remoteId != null
                && entry.remoteId == other.entry.remoteId
    }

    override fun hashCode(): Int {
        return entry.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is EntryItem
                && entry == other.entry
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
        val context = context
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