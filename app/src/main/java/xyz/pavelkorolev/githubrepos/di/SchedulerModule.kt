package xyz.pavelkorolev.githubrepos.di

import dagger.Module
import dagger.Provides
import xyz.pavelkorolev.githubrepos.services.AppSchedulerProvider
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider

@Module
class SchedulerModule {

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider = AppSchedulerProvider()

}
