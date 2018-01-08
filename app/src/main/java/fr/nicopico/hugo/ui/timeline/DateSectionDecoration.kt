package fr.nicopico.hugo.ui.timeline

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.ui.shared.SectionItemDecoration
import fr.nicopico.hugo.ui.shared.dp
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.getDateFormat
import java.util.*

class DateSectionDecoration(
        context: Context,
        adapter: TimelineAdapter
) : SectionItemDecoration<Timeline.Entry>(adapter), HugoLogger {

    private val inflater = LayoutInflater.from(context)
    private val dateFormat = getDateFormat(context)
    private val headerHeight = 24.dp(context)

    override fun sameHeader(itemA: Timeline.Entry, itemB: Timeline.Entry): Boolean {
        return itemA.time.toInt() == itemB.time.toInt()
    }

    override fun headerHeight(item: Timeline.Entry): Int = headerHeight

    override fun createHeaderView(parent: RecyclerView, item: Timeline.Entry): View {
        val sectionView = inflater.inflate(R.layout.item_timeline_section, parent, false)
        return sectionView.apply {
            val txtSection: TextView = findViewById(R.id.txtSection)
            txtSection.text = dateFormat.format(item.time)
        }
    }

    override fun getItem(parent: RecyclerView, position: Int): Timeline.Entry {
        return (adapter as TimelineAdapter).data[position]
    }

    @Suppress("DEPRECATION")
    private fun Date.toInt(): Int {
        return year * 1000 + month * 100 + date
    }
}