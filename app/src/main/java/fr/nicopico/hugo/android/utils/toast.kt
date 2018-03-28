package fr.nicopico.hugo.android.utils

import android.content.Context
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.widget.Toast

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Context.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, textId, duration).show()
}

fun Fragment.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, text, duration).show()
}

fun Fragment.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, textId, duration).show()
}