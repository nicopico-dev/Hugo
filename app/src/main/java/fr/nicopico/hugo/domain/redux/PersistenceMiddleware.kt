package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.services.PersistenceService
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

class PersistenceMiddleware(
        private val persistenceService: PersistenceService
) : Middleware<AppState> {

    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        when (action) {
            is SELECT_BABY -> // Persist the selected baby
                async { persistenceService.saveSelectedBaby(action.baby) }
            is BABY_ADDED -> async {
                if (persistenceService.isSelectedBaby(action.baby)) {
                    launch(UI) { store.dispatch(SELECT_BABY(action.baby)) }
                }
            }
        }
        return next.dispatch(action)
    }
}