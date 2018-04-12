package fr.nicopico.hugo.android.ui.timeline

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.HugoLogger
import fr.nicopico.hugo.android.ui.shared.SectionItemDecoration
import fr.nicopico.hugo.android.utils.dp
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.utils.getDateFormat
import java.util.*

class DateSectionDecoration(
        context: Context,
        private val adapter: TimelineAdapter
) : SectionItemDecoration<Timeline.Entry>(adapter), HugoLogger {

    private val inflater = LayoutInflater.from(context)
    private val dateFormat = getDateFormat(context)
    @Suppress("MagicNumber")
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
        return adapter.data[position]
    }

    @Suppress("DEPRECATION", "MagicNumber")
    private fun Date.toInt(): Int {
        return year * 1000 + month * 100 + date
    }
}