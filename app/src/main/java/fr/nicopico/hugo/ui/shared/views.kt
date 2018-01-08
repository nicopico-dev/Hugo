package fr.nicopico.hugo.ui.shared

import android.view.View

inline fun View.click(crossinline listener: (View) -> Unit) {
    setOnClickListener { listener.invoke(this) }
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.isVisible() = visibility == View.VISIBLE