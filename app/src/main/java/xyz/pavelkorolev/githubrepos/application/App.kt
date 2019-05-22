package xyz.pavelkorolev.githubrepos.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import xyz.pavelkorolev.githubrepos.di.schedulerModule
import xyz.pavelkorolev.githubrepos.di.serviceModule
import xyz.pavelkorolev.githubrepos.services.LoggingService

class App : Application() {

    private val loggingService: LoggingService by inject()

    override fun onCreate() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                listOf(
                    schedulerModule,
                    serviceModule
                )
            )
        }
        loggingService.setup()
    }

}
