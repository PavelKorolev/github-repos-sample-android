package xyz.pavelkorolev.githubrepos.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import xyz.pavelkorolev.githubrepos.di.AppComponent
import xyz.pavelkorolev.githubrepos.di.DaggerAppComponent
import xyz.pavelkorolev.githubrepos.di.SchedulerModule
import xyz.pavelkorolev.githubrepos.di.ServiceModule

class App : Application() {

    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
            .schedulerModule(SchedulerModule())
            .serviceModule(ServiceModule(this))
            .build()
    }

    override fun onCreate() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        super.onCreate()
        component.inject(this)
    }

}
