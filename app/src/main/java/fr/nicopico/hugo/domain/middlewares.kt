package fr.nicopico.hugo.domain

import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.debug
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

object LoggerMiddleware : Middleware<AppState> {
    private val logger = HugoLogger("actions")
    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        logger.debug { "Dispatching $action" }
        return next.dispatch(action)
    }
}