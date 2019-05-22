package xyz.pavelkorolev.githubrepos.ui.main

import android.content.Context
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import xyz.pavelkorolev.githubrepos.services.Navigator
import xyz.pavelkorolev.githubrepos.services.NavigatorImpl

val mainModule = module {
    factory<Navigator> { (context: Context) -> NavigatorImpl(context) }
}

val loadMainModule by lazy { loadKoinModules(mainModule) }
fun injectMainModule() = loadMainModule
