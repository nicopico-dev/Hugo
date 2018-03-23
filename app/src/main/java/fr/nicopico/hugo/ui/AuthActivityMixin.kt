package fr.nicopico.hugo.ui

import android.app.Activity
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import fr.nicopico.hugo.BuildConfig
import fr.nicopico.hugo.model.AnonymousUser
import fr.nicopico.hugo.redux.AUTHENTICATED
import fr.nicopico.hugo.redux.DISCONNECTED
import fr.nicopico.hugo.redux.EXIT_APP
import fr.nicopico.hugo.redux.REMOTE_ERROR
import fr.nicopico.hugo.redux.ReduxView
import fr.nicopico.hugo.service.authService
import fr.nicopico.hugo.service.convert
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.debug
import fr.nicopico.hugo.utils.warn

private const val RC_SIGN_IN = 42
private val AUTH_PROVIDERS = listOf(
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.EmailBuilder().build()
)

// TODO Restore support for anonymous login + account conversion (not supported by FirebaseUI yet :'( )
// see https://github.com/firebase/FirebaseUI-Android/issues/123
interface AuthActivityMixin : HugoLogger, ReduxView {

    val connected: Boolean
        get() = authService.currentUser?.let { it !is AnonymousUser } ?: false

    // Dirty fix for onActivityResult is never being called when the signIn activity is closed...
    var waitingForSignInResult: Boolean

    fun Activity.signInIfNeeded() {
        if (!connected) {
            debug("Not connected -> signIn")
            signIn()
        }
    }

    fun Activity.signIn() {
        val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(AUTH_PROVIDERS)
                .setIsSmartLockEnabled(BuildConfig.DEBUG.not(), true)
                .build()
        waitingForSignInResult = true
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun Activity.signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    dispatch(DISCONNECTED)
                }
    }

    // this cannot be an extension method as `super<AuthActivityMixin>.onActivityResult()` would not resolve
    // This isn't really an issue since we do not need the Activity instance anyway :)
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            waitingForSignInResult = false
            val response = IdpResponse.fromResultIntent(data)

            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser!!.convert()
                dispatch(AUTHENTICATED(user))
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    warn("User cancelled the authentication, exit the app")
                    dispatch(EXIT_APP)
                } else {
                    dispatch(REMOTE_ERROR(response.error!!))
                }
            }
        }
    }
}