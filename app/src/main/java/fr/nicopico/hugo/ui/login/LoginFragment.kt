package fr.nicopico.hugo.ui.login

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import fr.nicopico.hugo.BuildConfig
import fr.nicopico.hugo.R
import fr.nicopico.hugo.redux.AUTHENTICATED
import fr.nicopico.hugo.redux.EXIT_APP
import fr.nicopico.hugo.redux.REMOTE_ERROR
import fr.nicopico.hugo.redux.ReduxView
import fr.nicopico.hugo.service.convert
import fr.nicopico.hugo.ui.BaseFragment
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.warn

// TODO Restore support for anonymous login + account conversion (not supported by FirebaseUI yet :'( )
// see https://github.com/firebase/FirebaseUI-Android/issues/123
// TODO Move authentication code in the service layer
class LoginFragment : BaseFragment(), ReduxView, HugoLogger {

    companion object {
        private const val RC_SIGN_IN = 42
        private val AUTH_PROVIDERS = listOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build()
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(AUTH_PROVIDERS)
                .setIsSmartLockEnabled(BuildConfig.DEBUG.not(), true)
                .build()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            // Successfully signed in
            if (resultCode == RESULT_OK) {
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