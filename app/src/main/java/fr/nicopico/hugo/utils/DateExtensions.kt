package fr.nicopico.hugo.utils

import java.util.*
import java.util.concurrent.TimeUnit

fun Date.closeTo(other: Date, sigma: Long, sigmaUnit: TimeUnit): Boolean {
    return Math.abs(time - other.time) <= sigmaUnit.toMillis(sigma)
}