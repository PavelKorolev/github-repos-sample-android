package xyz.pavelkorolev.githubrepos.modules.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import xyz.pavelkorolev.githubrepos.application.RouterInput
import xyz.pavelkorolev.githubrepos.helpers.app
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var router: RouterInput

    private val component by lazy {
        app.component.plus(SplashModule(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        router.start()
    }

}
