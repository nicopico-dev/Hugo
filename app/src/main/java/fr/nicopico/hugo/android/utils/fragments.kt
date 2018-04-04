package fr.nicopico.hugo.android.utils

import android.support.v4.app.Fragment
import androidx.core.os.bundleOf
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T> T.withArguments(vararg args: Pair<String, Any?>): T where T : Fragment {
    arguments = bundleOf(*args)
    return this
}

@Suppress("UNCHECKED_CAST")
fun <T> Fragment.argument(argumentName: String) = object: ReadOnlyProperty<Fragment, T> {
    @Suppress("UNCHECKED_CAST")
    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return arguments!![argumentName] as T
    }
}