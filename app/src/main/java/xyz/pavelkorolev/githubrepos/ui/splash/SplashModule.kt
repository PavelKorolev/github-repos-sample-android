package xyz.pavelkorolev.githubrepos.ui.splash

import android.app.Activity
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import xyz.pavelkorolev.githubrepos.application.Router
import xyz.pavelkorolev.githubrepos.application.RouterInput

val splashModule = module {
    factory<RouterInput> { (activity: Activity) -> Router(activity) }
}

val loadSplashModule by lazy { loadKoinModules(splashModule) }
fun injectSplashModule() = loadSplashModule
