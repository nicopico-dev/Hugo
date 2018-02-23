package fr.nicopico.hugo.ui.shared

import android.support.annotation.DrawableRes
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView

typealias ViewListener = (View) -> Unit

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

inline fun View.click(crossinline listener: ViewListener) {
    setOnClickListener { listener.invoke(this) }
}

inline fun View.longClick(crossinline listener: ViewListener) {
    setOnLongClickListener {
        listener.invoke(this)
        return@setOnLongClickListener true
    }
}

fun TextView.drawables(
        @DrawableRes start: Int = 0,
        @DrawableRes top: Int = 0,
        @DrawableRes end: Int = 0,
        @DrawableRes bottom: Int = 0
) = setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)

fun TextView.isNotEmpty() = length() > 0

inline fun TextView.editorAction(crossinline listener: ViewListener) {
    setOnEditorActionListener { _, _, _ ->
        listener.invoke(this)
        return@setOnEditorActionListener false
    }
}

inline fun TextView.textChanged(crossinline listener: ViewListener) {
    addTextChangedListener(object : TextWatcher {

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener.invoke(this@textChanged)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // no-op
        }

        override fun afterTextChanged(s: Editable?) {
            // no-op
        }

    })
}