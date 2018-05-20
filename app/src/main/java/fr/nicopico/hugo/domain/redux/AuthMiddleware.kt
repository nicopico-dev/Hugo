package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.services.UserService
import redux.INIT
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

class AuthMiddleware(private val userService: UserService) : Middleware<AppState> {

    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        if (action == INIT) {
            userService.currentUser?.let { user ->
                store.dispatch(AUTHENTICATED(user))
            }
        }
        return next.dispatch(action)
    }
}