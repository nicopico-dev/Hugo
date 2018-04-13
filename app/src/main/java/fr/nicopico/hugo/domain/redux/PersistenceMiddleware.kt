package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.services.PersistenceService
import kotlinx.coroutines.experimental.async
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

class PersistenceMiddleware(
        private val persistenceService: PersistenceService
) : Middleware<AppState> {

    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        if (action is SELECT_BABY) {
            // Persist the selected baby
            async { persistenceService.saveBaby(action.baby) }
        }
        return next.dispatch(action)
    }
}