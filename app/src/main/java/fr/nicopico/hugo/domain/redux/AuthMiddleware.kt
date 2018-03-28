package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.services.AuthService
import redux.INIT
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

class AuthMiddleware(private val authService: AuthService) : Middleware<AppState> {

    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        if (action == INIT) {
            authService.currentUser?.let { user ->
                store.dispatch(AUTHENTICATED(user))
            }
        }
        return next.dispatch(action)
    }
}