package xyz.pavelkorolev.githubrepos.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.helper.transaction

abstract class BaseFragmentActivity : BaseActivity() {

    private lateinit var fragment: BaseFragment

    abstract fun rootFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        if (savedInstanceState == null) {
            fragment = rootFragment() as BaseFragment
            val extras = intent.extras
            if (extras != null && !extras.isEmpty) {
                fragment.arguments?.putAll(extras)
            }
            supportFragmentManager.transaction {
                replace(R.id.fragment_container, fragment)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fragment.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (fragment.onBackPressed()) {
            super.onBackPressed()
        }
    }

}
