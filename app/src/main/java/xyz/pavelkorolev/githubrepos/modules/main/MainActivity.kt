package xyz.pavelkorolev.githubrepos.modules.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.githubrepos.helpers.app
import xyz.pavelkorolev.githubrepos.modules.base.BaseActivity
import xyz.pavelkorolev.githubrepos.services.Navigator
import javax.inject.Inject

interface NavigationRoot {
    val contentFragmentManager: FragmentManager

    fun finish()
}

class MainActivity : BaseActivity(), NavigationRoot {

    private val component by lazy {
        app.component.plus(MainModule(this))
    }

    @Inject
    lateinit var navigator: Navigator

    override val contentFragmentManager: FragmentManager
        get() = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        setContentView(R.layout.activity_fragment)

        if (savedInstanceState == null) {
            navigator.rootOrganization()
        }
    }

    override fun onBackPressed() {
        navigator.back()
    }

    companion object {
        fun startIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

}
