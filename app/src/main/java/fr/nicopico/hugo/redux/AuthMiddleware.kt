package fr.nicopico.hugo.redux

import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.User
import fr.nicopico.hugo.service.AuthService
import fr.nicopico.hugo.utils.Result
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import redux.api.Dispatcher
import redux.api.Store
import redux.api.enhancer.Middleware

class AuthMiddleware(private val authService: AuthService) : Middleware<AppState> {

    override fun dispatch(store: Store<AppState>, next: Dispatcher, action: Any): Any {
        when (action) {
            SIGN_IN -> launch(UI) {
                val result: Result<User> = async { authService.signIn() }.await()
                when(result) {
                    is Result.Success -> store.dispatch(AUTHENTICATED(result.value))
                    is Result.Failure -> store.dispatch(REMOTE_ERROR(result.error))
                }
            }
        }
        return next.dispatch(action)
    }
}