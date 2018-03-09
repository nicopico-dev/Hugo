@file:Suppress(
        "unused",
        "NOTHING_TO_INLINE", "NAME_SHADOWING",
        "FunctionName", "FunctionNaming", "TooManyFunctions"
)

package fr.nicopico.hugo.utils

import android.util.Log
import fr.nicopico.hugo.BuildConfig

interface HugoLogger {
    /**
     * The logger tag used in extension functions for the [HugoLogger].
     * Note that the tag length should not be more than 23 symbols.
     */
    val loggerTag: String
        get() = getTag(javaClass)
}

private const val MAX_TAG_LENGTH = 23

fun HugoLogger(clazz: Class<*>): HugoLogger = object : HugoLogger {
    override val loggerTag = getTag(clazz)
}

fun HugoLogger(tag: String): HugoLogger = object : HugoLogger {
    init {
        assert(tag.length <= MAX_TAG_LENGTH) { "The maximum tag length is $MAX_TAG_LENGTH, got $tag" }
    }

    override val loggerTag = tag
}

inline fun <reified T : Any> HugoLogger(): HugoLogger = HugoLogger(T::class.java)

//region Logger methods
inline fun HugoLogger.verbose(m: () -> String) {
    if (BuildConfig.DEBUG) {
        doLog(this, Log.VERBOSE, m())
    }
}

inline fun HugoLogger.verbose(m: String) {
    if (BuildConfig.DEBUG) {
        doLog(this, Log.VERBOSE, m)
    }
}

inline fun HugoLogger.debug(m: () -> String) {
    if (BuildConfig.DEBUG) {
        doLog(this, Log.DEBUG, m())
    }
}

inline fun HugoLogger.debug(m: String) {
    if (BuildConfig.DEBUG) {
        doLog(this, Log.DEBUG, m)
    }
}

inline fun HugoLogger.info(m: () -> String) {
    if (BuildConfig.DEBUG) {
        doLog(this, Log.INFO, m())
    }
}

inline fun HugoLogger.info(m: String) {
    if (BuildConfig.DEBUG) {
        doLog(this, Log.INFO, m)
    }
}

inline fun HugoLogger.warn(error: Throwable? = null, m: () -> String) {
    doLog(this, Log.WARN, m(), error)
}

inline fun HugoLogger.warn(error: Throwable? = null, m: String) {
    doLog(this, Log.WARN, m, error)
}

inline fun HugoLogger.error(error: Throwable? = null, m: () -> String) {
    doLog(this, Log.ERROR, m(), error)
}

inline fun HugoLogger.error(error: Throwable? = null, m: String) {
    doLog(this, Log.ERROR, m, error)
}
//endregion

@Suppress("ComplexMethod")
fun doLog(logger: HugoLogger, level: Int, message: String, error: Throwable? = null) {
    val tag = logger.loggerTag
    if (error != null) {
        when (level) {
            Log.VERBOSE -> Log.v(tag, message, error)
            Log.DEBUG -> Log.d(tag, message, error)
            Log.INFO -> Log.i(tag, message, error)
            Log.WARN -> Log.w(tag, message, error)
            Log.ERROR -> Log.e(tag, message, error)
        }
    } else {
        when (level) {
            Log.VERBOSE -> Log.v(tag, message)
            Log.DEBUG -> Log.d(tag, message)
            Log.INFO -> Log.i(tag, message)
            Log.WARN -> Log.w(tag, message)
            Log.ERROR -> Log.e(tag, message)
        }
    }
}

private fun getTag(clazz: Class<*>): String {
    val tag = clazz.simpleName
    return if (tag.length <= MAX_TAG_LENGTH) {
        tag
    } else {
        tag.substring(0, MAX_TAG_LENGTH)
    }
}