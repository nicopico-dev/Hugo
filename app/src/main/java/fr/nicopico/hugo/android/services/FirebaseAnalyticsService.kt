package fr.nicopico.hugo.android.services

import android.content.Context
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics

import fr.nicopico.hugo.domain.services.AnalyticEvent
import fr.nicopico.hugo.domain.services.AnalyticProperty
import fr.nicopico.hugo.domain.services.AnalyticsService

class FirebaseAnalyticsService(context: Context) : AnalyticsService {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun sendEvent(event: AnalyticEvent) {
        val params = when {
            event.params.isNotEmpty() -> bundleOf(*event.params)
            else -> null
        }
        firebaseAnalytics.logEvent(event.eventName, params)
    }

    override fun setProperty(property: AnalyticProperty) {
        firebaseAnalytics.setUserProperty(property.propertyName, property.value)
    }
}