package fr.nicopico.hugo.domain.services

import fr.nicopico.hugo.domain.model.CareType

interface AnalyticsService {
    fun sendEvent(event: AnalyticEvent)
    fun setProperty(property: AnalyticProperty)
    fun setCurrentScreen(screenName: String)
}

sealed class AnalyticEvent(
    val eventName: String,
    vararg val params: Pair<String, Any>
) {
    object CreateBaby : AnalyticEvent("baby_create")
    object SelectBaby : AnalyticEvent("baby_select")
    object RemoveBaby : AnalyticEvent("baby_remove")

    data class AddEntry(val type: CareType)
        : AnalyticEvent("timeline_add", "careType" to type.name)

    data class EditEntry(val type: CareType)
        : AnalyticEvent("timeline_edit", "careType" to type.name)

    data class RemoveEntry(val type: CareType)
        : AnalyticEvent("timeline_remove", "careType" to type.name)
}

sealed class AnalyticProperty(
        val propertyName: String,
        val value: String?
) {
    class BabyCount(value: Int?) : AnalyticProperty("babyCount", value?.toString())
}