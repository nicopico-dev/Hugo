package fr.nicopico.hugo.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.Screen
import fr.nicopico.hugo.redux.DISCONNECTED
import fr.nicopico.hugo.redux.GO_BACK
import fr.nicopico.hugo.redux.ON_EXIT_APP
import fr.nicopico.hugo.redux.ReduxLifecycleListener
import fr.nicopico.hugo.redux.ReduxView
import fr.nicopico.hugo.redux.appStore
import fr.nicopico.hugo.ui.babies.BabySelectionFragment
import fr.nicopico.hugo.ui.login.LoginFragment
import fr.nicopico.hugo.ui.timeline.TimelineFragment
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.debug
import fr.nicopico.hugo.utils.info


class MainActivity : AppCompatActivity(), HugoLogger, ReduxView {

    private val connected: Boolean
        get() = appStore.state.user != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ReduxLifecycleListener(::updateScreen)
                .restrictOn { s1, s2 ->
                    s1.screen != s2.screen
                }
                .subscribe(this)
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

    override fun onBackPressed() {
        // DO NOT CALL super.onBackPressed()
        dispatch(GO_BACK)
    }

    private fun updateScreen(state: AppState) {
        val screen = state.screen
        if (screen == Screen.Exit) {
            info("Exiting the application")
            dispatch(ON_EXIT_APP)
            finish()
            return
        }

        if (screen == Screen.Login) {
            // Special case for Screen.Login, because we use startActivityForResult with FirebaseUI
            val fragment = supportFragmentManager.findFragmentById(R.id.formContainer)
            if (fragment is LoginFragment) return
        }

        debug { "Switch screen to $screen" }
        val fragment: BaseFragment = when(screen) {
            Screen.Exit -> throw IllegalStateException("EXIT")
            Screen.Login -> LoginFragment()
            Screen.BabySelection -> BabySelectionFragment()
            Screen.Timeline -> TimelineFragment()
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.formContainer, fragment)
                .commit()
    }

    private fun signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    dispatch(DISCONNECTED)
                }
    }
}