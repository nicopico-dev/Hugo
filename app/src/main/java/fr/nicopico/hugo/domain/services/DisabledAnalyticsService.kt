package fr.nicopico.hugo.domain.services

import fr.nicopico.hugo.android.HugoLogger
import fr.nicopico.hugo.android.debug

class DisabledAnalyticsService : AnalyticsService, HugoLogger {

    override fun sendEvent(event: AnalyticEvent) {
        debug("Send event $event")
    }

    override fun setProperty(property: AnalyticProperty) {
        debug("Set property $property")
    }
}