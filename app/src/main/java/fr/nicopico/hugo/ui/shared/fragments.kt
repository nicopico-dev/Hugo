package fr.nicopico.hugo.ui.shared

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import java.io.Serializable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun bundle(vararg args: Pair<String, Any?>) = Bundle().apply {
    args.forEach { (key, value) ->
        when (value) {
            is String -> putString(key, value)
            is CharSequence -> putCharSequence(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Bundle -> putBundle(key, value)
            is Parcelable -> putParcelable(key, value)
            is Serializable -> putSerializable(key, value)
            else -> throw UnsupportedOperationException("type of $value is not supported")
        }
    }
}

fun <T> T.withArguments(vararg args: Pair<String, Any?>): T where T : Fragment {
    arguments = bundle(*args)
    return this
}

@Suppress("UNCHECKED_CAST")
fun <T> Fragment.argument(argumentName: String) = object: ReadOnlyProperty<Fragment, T> {
    @Suppress("UNCHECKED_CAST")
    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return arguments!![argumentName] as T
    }
}
