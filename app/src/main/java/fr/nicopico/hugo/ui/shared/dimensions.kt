package fr.nicopico.hugo.ui.shared

import android.content.Context
import android.support.annotation.DimenRes
import android.support.v4.app.Fragment

fun Int.dp(context: Context, forSize: Boolean = false): Int {
    val px = Math.round(this * context.resources.displayMetrics.density)
    return when {
        // Ensure px is greater than 0
        forSize -> Math.max(px, 1)
        else -> px
    }
}

fun Context.dimensionForSize(@DimenRes dimen: Int): Int = resources.getDimensionPixelSize(dimen)
fun Context.dimensionForOffset(@DimenRes dimen: Int): Int = resources.getDimensionPixelOffset(dimen)

fun Fragment.dimensionForSize(@DimenRes dimen: Int): Int = resources.getDimensionPixelSize(dimen)
fun Fragment.dimensionForOffset(@DimenRes dimen: Int): Int = resources.getDimensionPixelOffset(dimen)
