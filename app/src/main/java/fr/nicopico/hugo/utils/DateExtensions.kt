package fr.nicopico.hugo.utils

import android.content.Context
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun getDateFormat(context: Context): DateFormat = android.text.format.DateFormat.getMediumDateFormat(context)
fun getTimeFormat(context: Context): DateFormat = android.text.format.DateFormat.getTimeFormat(context)

fun Date.closeTo(other: Date, sigma: Long, sigmaUnit: TimeUnit): Boolean {
    return Math.abs(time - other.time) <= sigmaUnit.toMillis(sigma)
}

fun Date.withHourMinute(hour: Int, minute: Int): Date {
    return Calendar.getInstance().apply {
        time = this@withHourMinute
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}

fun Date.withYearMonthDay(year: Int, month: Int, day: Int): Date {
    return Calendar.getInstance().apply {
        time = this@withYearMonthDay
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DATE, day)
    }.time
}

fun Date.round(): Date {
    return Calendar.getInstance().apply {
        time = this@round
        set(Calendar.MINUTE, get(Calendar.MINUTE).roundToFive())
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}

private fun Int.roundToFive(): Int {
    // TODO Round to closest value
    return this - this % 5
}