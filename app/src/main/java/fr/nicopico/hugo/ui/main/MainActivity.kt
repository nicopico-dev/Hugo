package fr.nicopico.hugo.ui.main

import android.os.Bundle
import fr.nicopico.hugo.R
import fr.nicopico.hugo.ui.BaseActivity
import fr.nicopico.hugo.ui.timeline.TimelineFragment

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, TimelineFragment())
                    .commit()
        }
    }
}