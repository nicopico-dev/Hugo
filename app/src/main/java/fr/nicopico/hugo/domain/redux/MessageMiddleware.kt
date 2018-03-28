package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.domain.model.AppState
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

/**
 * Automatically remove transient message from state
 */
class MessageMiddleware : Middleware<AppState> {
    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        val result = next.dispatch(action)
        if (action is DISPLAY_MESSAGE && action.message.transient) {
            store.dispatch(REMOVE_MESSAGE(action.message))
        }
        return result
    }
}