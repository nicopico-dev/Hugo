package fr.nicopico.hugo.utils

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import java.io.Serializable

fun <T> T.withArguments(vararg args: Pair<String, Any?>): T where T : Fragment {
    arguments = Bundle().apply {
        args.forEach { (key, value) ->
            when(value) {
                is String -> putString(key, value)
                is CharSequence -> putCharSequence(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Parcelable -> putParcelable(key, value)
                is Serializable -> putSerializable(key, value)
                else -> throw UnsupportedOperationException("type of $value is not supported")
            }
        }
    }
    return this
}

