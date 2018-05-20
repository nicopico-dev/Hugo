@file:Suppress("FunctionName")

package fr.nicopico.hugo.android.services

import com.google.firebase.Timestamp

// Firebase store numbers as Long, but we want Int
fun Any?.fsInt() = (this as Long).toInt()
fun Any?.fsIntOrNull() = (this as Long?)?.toInt()
fun Any?.fsLong() = this as Long

// Firebase store date as Timestamp
fun Any?.fsDate(): java.util.Date = (this as Timestamp).toDate()

fun Any?.fsString(): String = this as String
fun Any?.fsStringOrNull(): String? = this as? String

fun Any?.fsList(): Iterable<*> = this as? Iterable<*> ?: emptyList<Any?>()
inline fun <reified T : Enum<T>> Any?.fsEnum(): T = enumValueOf(this as String)
typealias FsMap = Map<*, *>