package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.debug
import fr.nicopico.hugo.utils.verbose
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

object LoggerMiddleware : Middleware<AppState> {
    private val logger = HugoLogger("REDUX")
    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        logger.debug { "Dispatching $action" }
        return next.dispatch(action).also {
            logger.verbose { "State: ${store.state}" }
        }
    }
}