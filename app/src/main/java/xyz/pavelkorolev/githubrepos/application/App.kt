package xyz.pavelkorolev.githubrepos.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import xyz.pavelkorolev.githubrepos.di.AppComponent
import xyz.pavelkorolev.githubrepos.di.DaggerAppComponent

class App : Application() {

    val component: AppComponent by lazy {
        DaggerAppComponent.create()
    }

    override fun onCreate() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        super.onCreate()
        component.inject(this)
    }

}
