package fr.nicopico.hugo.ui.shared

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import kotlin.properties.Delegates

// TODO Handle reverse layout
abstract class SectionItemDecoration<T>(
        adapter: RecyclerView.Adapter<*>
) : RecyclerView.ItemDecoration() {

    private val headerViews: SparseArray<View> = SparseArray()
    private val bounds = Rect()

    var adapter by Delegates.observable(adapter) { _, previous, new ->
        clear()
        previous.unregisterAdapterDataObserver(adapterDataObserver)
        new.registerAdapterDataObserver(adapterDataObserver)
    }
    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            clear()
        }
    }

    protected abstract fun sameHeader(itemA: T, itemB: T): Boolean
    protected abstract fun headerHeight(item: T): Int
    protected abstract fun createHeaderView(parent: RecyclerView, item: T): View
    protected abstract fun getItem(parent: RecyclerView, position: Int): T

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        if (hasHeader(parent, position)) {
            val item = getItem(parent, position)
            val headerHeight = headerHeight(item)
            val headerView = createHeaderView(parent, item).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight)
            }
            headerViews.put(position, headerView)
            outRect.set(0, headerHeight, 0, 0)
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        var range: IntProgression = 0 until parent.childCount
        if (parent.reverseLayout()) {
            range = range.reversed()
        }

        for (i in range) {
            val child = parent.getChildAt(i)

            val position = parent.getChildAdapterPosition(child)
            val headerView = headerViews.get(position) ?: continue

            // Prepare headerView for rendering
            headerView.measure(
                    View.MeasureSpec.makeMeasureSpec(right - left, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            headerView.layout(0, 0, headerView.measuredWidth, headerView.measuredHeight)

            // Retrieve bounds of the child to draw the header on top
            parent.getDecoratedBoundsWithMargins(child, bounds)

            c.save()
            c.translate(left.toFloat(), bounds.top.toFloat())
            headerView.draw(c)
            c.restore()
        }
    }

    protected open fun hasHeader(parent: RecyclerView, position: Int): Boolean {
        return if (parent.reverseLayout()) {
            position == parent.adapter.itemCount - 1
                    || !sameHeader(getItem(parent, position), getItem(parent, position + 1))
        } else {
            position == 0
                    || !sameHeader(getItem(parent, position), getItem(parent, position - 1))
        }
    }

    private fun clear() {
        headerViews.clear()
    }

    private fun RecyclerView.reverseLayout(): Boolean {
        val lm = layoutManager
        return when (lm) {
            is LinearLayoutManager -> lm.reverseLayout
            is GridLayoutManager -> lm.reverseLayout
            else -> false
        }
    }
}
