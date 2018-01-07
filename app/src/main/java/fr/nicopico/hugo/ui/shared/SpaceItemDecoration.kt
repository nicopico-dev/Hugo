package fr.nicopico.hugo.ui.shared

import android.graphics.Rect
import android.support.annotation.Px
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View


class SpaceItemDecoration(
        private val layoutManager: RecyclerView.LayoutManager,
        @field:Px private val spaceSize: Int
) : RecyclerView.ItemDecoration() {

    private val halfSize: Int = spaceSize / 2
    private val columnCount: Int = when (layoutManager) {
        is GridLayoutManager -> layoutManager.spanCount
        else -> 1
    }
    private var spanSizeLookup: GridLayoutManager.SpanSizeLookup? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)

        val firstColumn: Boolean
        val lastColumn: Boolean

        val spanSizeLookup = getSpanSizeLookup()
        if (spanSizeLookup != null) {
            // Use spanSizeLookup as some items may span multiple columns
            val startSpanIndex = spanSizeLookup.getSpanIndex(position, columnCount)
            val endSpanIndex = startSpanIndex + spanSizeLookup.getSpanSize(position)
            firstColumn = startSpanIndex == 0
            lastColumn = endSpanIndex == columnCount
        } else {
            val column = position % columnCount
            firstColumn = column == 0
            lastColumn = column == columnCount - 1
        }

        // Spread horizontal spacing to keep the same width for each columns
        outRect.set(
                if (firstColumn) 0 else halfSize,
                0,
                if (lastColumn) 0 else halfSize,
                spaceSize
        )
    }

    private fun getSpanSizeLookup(): GridLayoutManager.SpanSizeLookup? {
        if (spanSizeLookup == null && layoutManager is GridLayoutManager) {
            spanSizeLookup = layoutManager.spanSizeLookup.apply {
                // Enable spanIndexCache to improve performance
                isSpanIndexCacheEnabled = true
            }
        }

        return spanSizeLookup
    }
}