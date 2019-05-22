package xyz.pavelkorolev.githubrepos.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.parameter.parametersOf
import xyz.pavelkorolev.githubrepos.application.RouterInput

class SplashActivity : AppCompatActivity() {

    private val router: RouterInput by inject { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectSplashModule()
        router.start()
    }

}
