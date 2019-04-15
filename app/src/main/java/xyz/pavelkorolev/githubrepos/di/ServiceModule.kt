package xyz.pavelkorolev.githubrepos.di

import dagger.Module
import dagger.Provides
import xyz.pavelkorolev.githubrepos.BuildConfig
import xyz.pavelkorolev.githubrepos.application.App
import xyz.pavelkorolev.githubrepos.services.*
import javax.inject.Singleton

@Module
class ServiceModule(val app: App) {

    @Provides
    @Singleton
    fun provideApiService(): ApiService = ApiServiceImpl(BuildConfig.DEBUG)

    @Provides
    @Singleton
    fun provideLoggingService(): LoggingService = LoggingServiceImpl(BuildConfig.DEBUG)

    @Provides
    @Singleton
    fun provideImageLoader(): ImageLoader = ImageLoader(app)

    @Provides
    @Singleton
    fun provideServerUserMapper(): ServerUserMapper = ServerUserMapper()

    @Provides
    @Singleton
    fun provideServerRepositoryMapper(): ServerRepositoryMapper = ServerRepositoryMapper()

}
