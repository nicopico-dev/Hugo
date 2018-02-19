package fr.nicopico.hugo.ui.shared

import android.support.annotation.DrawableRes
import android.view.View
import android.widget.TextView

inline fun View.click(crossinline listener: (View) -> Unit) {
    setOnClickListener { listener.invoke(this) }
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.visible(visible: Boolean) {
    visibility = if (visible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun View.isVisible() = visibility == View.VISIBLE

fun TextView.drawables(
        @DrawableRes start: Int = 0,
        @DrawableRes top: Int = 0,
        @DrawableRes end: Int = 0,
        @DrawableRes bottom: Int = 0
) = setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)