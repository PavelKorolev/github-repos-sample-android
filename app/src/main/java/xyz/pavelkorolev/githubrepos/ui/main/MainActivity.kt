package xyz.pavelkorolev.githubrepos.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.githubrepos.services.Navigator
import xyz.pavelkorolev.githubrepos.ui.base.BaseActivity

interface NavigationRoot {
    val contentFragmentManager: FragmentManager

    fun finish()
}

class MainActivity : BaseActivity(), NavigationRoot {

    private val navigator: Navigator by inject { parametersOf(this) }

    override val contentFragmentManager: FragmentManager
        get() = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        injectMainModule()

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
