package fr.nicopico.hugo.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import fr.nicopico.hugo.R
import fr.nicopico.hugo.redux.SIGN_IN
import fr.nicopico.hugo.redux.appStore
import fr.nicopico.hugo.ui.BaseActivity
import fr.nicopico.hugo.ui.shared.toast
import redux.api.Store


class MainActivity : BaseActivity() {

    private var subscription: Store.Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val state = appStore.state
        if (state.user == null) {
            appStore.dispatch(SIGN_IN)
        }
    }

    override fun onResume() {
        super.onResume()
        subscription = appStore.subscribe {
            toast("${appStore.state}")
        }
    }

    override fun onPause() {
        super.onPause()
        subscription?.unsubscribe()
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