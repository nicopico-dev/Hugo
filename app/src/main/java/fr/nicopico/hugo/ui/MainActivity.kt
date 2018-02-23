package fr.nicopico.hugo.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.AppState
import fr.nicopico.hugo.redux.ReduxLifecycleListener
import fr.nicopico.hugo.ui.babies.BabySelectionFragment
import fr.nicopico.hugo.ui.login.LoginFragment
import fr.nicopico.hugo.ui.timeline.TimelineFragment
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.debug


class MainActivity : BaseActivity(), HugoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycle.addObserver(ReduxLifecycleListener(::updateScreen))
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

    private fun updateScreen(state: AppState) {
        val screen = when {
            state.user == null -> LoginFragment.SCREEN
            state.selectedBaby == null -> BabySelectionFragment.SCREEN
            else -> TimelineFragment.SCREEN
        }

        val currentScreen = supportFragmentManager.findFragmentById(R.id.formContainer).let {
            (it as? BaseFragment)?.screen
        }
        if (screen == currentScreen) return

        debug { "Switch screen to $screen" }
        val fragment = when(screen) {
            LoginFragment.SCREEN -> LoginFragment()
            BabySelectionFragment.SCREEN -> BabySelectionFragment()
            TimelineFragment.SCREEN -> TimelineFragment()
            else -> throw UnsupportedOperationException("Unknown screen $screen")
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.formContainer, fragment)
                .commit()
    }
}