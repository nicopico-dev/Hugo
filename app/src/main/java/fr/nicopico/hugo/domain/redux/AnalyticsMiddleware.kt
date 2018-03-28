package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.services.AnalyticEvent
import fr.nicopico.hugo.domain.services.AnalyticProperty
import fr.nicopico.hugo.domain.services.AnalyticsService
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

class AnalyticsMiddleware(
        private val analyticsService: AnalyticsService
) : Middleware<AppState> {
    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        when (action) {
            is ADD_BABY -> AnalyticEvent.CreateBaby.send()
            is SELECT_BABY -> AnalyticEvent.SelectBaby.send()
            is REMOVE_BABY -> AnalyticEvent.RemoveBaby.send()

            is ADD_ENTRY -> AnalyticEvent.AddEntry(action.entry.type).send()
            is UPDATE_ENTRY -> AnalyticEvent.EditEntry(action.entry.type).send()
            is REMOVE_ENTRY -> AnalyticEvent.RemoveEntry(action.entry.type).send()
        }

        return next.dispatch(action).also {
            // After the state has been updated
            when (action) {
                is BABY_ADDED, is BABY_REMOVED -> AnalyticProperty.BabyCount(store.state.babies.size).send()
            }
        }
    }

    private fun AnalyticEvent.send() = analyticsService.sendEvent(this)
    private fun AnalyticProperty.send() = analyticsService.setProperty(this)
}