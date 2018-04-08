package fr.nicopico.hugo.android.utils

import android.app.Activity

interface ActivityProvider {
    val currentActivity: Activity?
}