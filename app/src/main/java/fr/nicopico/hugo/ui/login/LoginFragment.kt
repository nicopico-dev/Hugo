package fr.nicopico.hugo.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.nicopico.hugo.R
import fr.nicopico.hugo.redux.SIGN_IN
import fr.nicopico.hugo.redux.appStore
import fr.nicopico.hugo.ui.BaseFragment

class LoginFragment : BaseFragment() {

    companion object {
        const val SCREEN = "SCREEN_LOGIN"
    }

    override val screen: String = SCREEN

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appStore.dispatch(SIGN_IN)
    }
}