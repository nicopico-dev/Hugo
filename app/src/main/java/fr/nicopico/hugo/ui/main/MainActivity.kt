package fr.nicopico.hugo.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import fr.nicopico.hugo.R
import fr.nicopico.hugo.model.Baby
import fr.nicopico.hugo.redux.*
import fr.nicopico.hugo.ui.BaseActivity
import fr.nicopico.hugo.ui.timeline.TimelineFragment


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            appStore.subscribe {
                with(appStore.state) {
                    when {
                        babies.isEmpty() -> appStore.dispatch(ADD_BABY(Baby("Hugo")))
                        selectedBaby == null -> appStore.dispatch(SELECT_BABY(babies[0]))
                        else -> supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.container, TimelineFragment())
                                .commit()
                    }
                    null
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appStore.dispatch(FETCH_BABIES)
    }

    override fun onPause() {
        super.onPause()
        appStore.dispatch(STOP_FETCHING_BABIES)
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
}