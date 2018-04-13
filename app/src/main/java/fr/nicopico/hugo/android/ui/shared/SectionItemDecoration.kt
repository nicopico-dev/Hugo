package fr.nicopico.hugo.android.ui.shared

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup

abstract class SectionItemDecoration<T>(
        private val adapter: RecyclerView.Adapter<*>
) : RecyclerView.ItemDecoration() {

    private val headerViews: SparseArray<View> = SparseArray()
    private val bounds = Rect()
    private val dataObserver = DataObserver()
    private var rcv: RecyclerView? = null

    init {
        adapter.registerAdapterDataObserver(dataObserver)
    }

    fun cleanUp() {
        adapter.unregisterAdapterDataObserver(dataObserver)
        rcv = null
    }

    protected abstract fun sameHeader(itemA: T, itemB: T): Boolean
    protected abstract fun headerHeight(item: T): Int
    protected abstract fun createHeaderView(parent: RecyclerView, item: T): View
    protected abstract fun getItem(parent: RecyclerView, position: Int): T

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        rcv = parent

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

        for (i in 0 until parent.childCount) {
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
        return when (position) {
            0 -> true
            RecyclerView.NO_POSITION -> false
            else -> !sameHeader(getItem(parent, position), getItem(parent, position - 1))
        }
    }

    private fun refresh() {
        headerViews.clear()
        rcv?.invalidateItemDecorations()
    }

    private inner class DataObserver : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            refresh()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            refresh()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            refresh()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            refresh()
        }

        override fun onChanged() {
            refresh()
        }
    }
}
