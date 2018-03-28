package fr.nicopico.hugo.android.utils

import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog

fun confirm(context: Context,
            @StringRes message: Int, @StringRes positiveText: Int, @StringRes cancelText: Int,
            action: () -> Unit) {

    AlertDialog.Builder(context)
            .setMessage(message)
            .setCancelable(true)
            .setNeutralButton(cancelText) { _, _ -> Unit }
            .setPositiveButton(positiveText) { _, _ -> action.invoke() }
            .show()
}