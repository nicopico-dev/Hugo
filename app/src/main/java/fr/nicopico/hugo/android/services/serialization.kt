package fr.nicopico.hugo.android.services

/**
 * Firebase store numbers as Long, but we want Int
 */
fun Any?.asInt() = (this as Long).toInt()
fun Any?.asIntOrNull() = (this as Long?)?.toInt()
inline fun <reified T : Enum<T>> Any?.asEnum(): T = enumValueOf(this as String)