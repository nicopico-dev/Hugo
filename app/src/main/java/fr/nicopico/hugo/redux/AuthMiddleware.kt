package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.service.AuthService
import kotlinx.coroutines.experimental.async
import redux.INIT
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

class AuthMiddleware(private val authService: AuthService) : Middleware<AppState> {
    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        when (action) {
            INIT -> authService.apply {
                addOnUserChangeListener {
                    store.dispatch(AUTHENTICATED(it))
                }
                async { signIn() }
            }
        }
        return next.dispatch(action)
    }
}