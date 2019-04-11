package xyz.pavelkorolev.githubrepos.di

import dagger.Module
import dagger.Provides
import xyz.pavelkorolev.githubrepos.BuildConfig
import xyz.pavelkorolev.githubrepos.application.App
import xyz.pavelkorolev.githubrepos.services.ApiService
import xyz.pavelkorolev.githubrepos.services.ApiServiceImpl
import xyz.pavelkorolev.githubrepos.services.LoggingService
import xyz.pavelkorolev.githubrepos.services.LoggingServiceImpl
import javax.inject.Singleton

@Module
class ServiceModule(val app: App) {

    @Provides
    @Singleton
    fun provideApiService(): ApiService = ApiServiceImpl(BuildConfig.DEBUG)

    @Provides
    @Singleton
    fun provideLoggingService(): LoggingService = LoggingServiceImpl(BuildConfig.DEBUG)

}
