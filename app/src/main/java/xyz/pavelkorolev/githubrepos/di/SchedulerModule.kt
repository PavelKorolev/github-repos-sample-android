package xyz.pavelkorolev.githubrepos.di

import org.koin.dsl.module
import xyz.pavelkorolev.githubrepos.services.AppSchedulerProvider
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider

val schedulerModule = module {
    single<SchedulerProvider> { AppSchedulerProvider() }
}