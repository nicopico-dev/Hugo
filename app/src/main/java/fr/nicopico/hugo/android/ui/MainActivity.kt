package fr.nicopico.hugo.android.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.*
import fr.nicopico.hugo.android.ui.babies.BabySelectionFragment
import fr.nicopico.hugo.android.ui.timeline.TimelineFragment
import fr.nicopico.hugo.domain.model.AppState
import fr.nicopico.hugo.domain.model.Screen
import fr.nicopico.hugo.domain.redux.GO_BACK
import fr.nicopico.hugo.domain.redux.ON_APP_EXIT
import fr.nicopico.hugo.domain.services.AuthService


class MainActivity : AppCompatActivity(),
        ReduxView, ReduxDispatcher, StateProvider,
        AuthActivityMixin, HugoLogger {

    //region AuthActivityMixin
    override var waitingForSignInResult: Boolean = false
    override val authService: AuthService
        get() = (application as App).authService
    //endregion

    //region Redux
    override fun dispatch(action: Any) = _dispatch(action)
    override fun subscribe(subscriber: () -> Unit) = _subscribe(subscriber)
    override val state: AppState
        get() = _state
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ReduxLifecycleListener
                .create(this)
                .restrictOn { s1, s2 ->
                    s1.screen != s2.screen
                }
                .observe()
    }

    override fun onDestroy() {
        super.onDestroy()
        dispatch(ON_APP_EXIT)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu?.findItem(R.id.menu_sign_out)?.isVisible = connected
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            item.itemId == R.id.menu_licenses -> {
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
                true
            }
            item.itemId == R.id.menu_sign_out -> { signOut(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<AppCompatActivity>.onActivityResult(requestCode, resultCode, data)
        super<AuthActivityMixin>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        // DO NOT CALL super.onBackPressed()
        dispatch(GO_BACK)
    }

    override fun render(state: AppState) {
        val screen = state.screen
        // Special case for Screen.Exit
        if (screen == Screen.Exit
                || (waitingForSignInResult && !connected)) {
            info("Exiting the application")
            finish()
            return
        }

        debug { "Switch screen to $screen" }
        val fragment: Fragment = when(screen) {
            Screen.Exit -> {
                // Screen.Exit is a special case
                throw IllegalStateException("EXIT")
            }
            Screen.Loading -> LoadingFragment()
            Screen.BabySelection -> BabySelectionFragment()
            Screen.Timeline -> TimelineFragment()
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.formContainer, fragment)
                .commit()

        if (screen == Screen.Loading) {
            signInIfNeeded()
        }
    }
}

class LoadingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }
}