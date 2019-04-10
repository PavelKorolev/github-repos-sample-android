package xyz.pavelkorolev.githubrepos.di

import dagger.Module
import dagger.Provides
import xyz.pavelkorolev.githubrepos.BuildConfig
import xyz.pavelkorolev.githubrepos.application.App
import xyz.pavelkorolev.githubrepos.services.ApiService
import xyz.pavelkorolev.githubrepos.services.ApiServiceImpl

@Module
class ServiceModule(val app: App) {

    @Provides
    fun provideApiService(): ApiService = ApiServiceImpl(BuildConfig.DEBUG)

}
