package fr.nicopico.hugo.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.model.Screen
import fr.nicopico.hugo.redux.EXIT_APP
import fr.nicopico.hugo.redux.GO_BACK
import fr.nicopico.hugo.redux.ReduxLifecycleListener
import fr.nicopico.hugo.redux.ReduxView
import fr.nicopico.hugo.ui.babies.BabySelectionFragment
import fr.nicopico.hugo.ui.login.LoginFragment
import fr.nicopico.hugo.ui.timeline.TimelineFragment
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.debug
import fr.nicopico.hugo.utils.info


class MainActivity : BaseActivity(), HugoLogger, ReduxView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycle.addObserver(ReduxLifecycleListener(::updateScreen) { s1, s2 ->
            s1.screen != s2.screen
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_licenses) {
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        // DO NOT CALL super.onBackPressed()
        dispatch(GO_BACK)
    }

    private fun updateScreen(state: AppState) {
        val screen = state.screen
        if (screen == Screen.Exit) {
            info("Exiting the application")
            dispatch(EXIT_APP)
            finish()
            return
        }

        debug { "Switch screen to $screen" }
        val fragment = when(screen) {
            Screen.Exit -> throw IllegalStateException("EXIT")
            Screen.Login -> LoginFragment()
            Screen.BabySelection -> BabySelectionFragment()
            Screen.Timeline -> TimelineFragment()
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.formContainer, fragment)
                .commit()
    }
}