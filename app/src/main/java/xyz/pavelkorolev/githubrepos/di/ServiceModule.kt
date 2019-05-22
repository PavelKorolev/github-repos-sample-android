package xyz.pavelkorolev.githubrepos.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import xyz.pavelkorolev.githubrepos.BuildConfig
import xyz.pavelkorolev.githubrepos.network.ApiExceptionMapper
import xyz.pavelkorolev.githubrepos.network.ApiExceptionMapperImpl
import xyz.pavelkorolev.githubrepos.network.ApiService
import xyz.pavelkorolev.githubrepos.network.ApiServiceImpl
import xyz.pavelkorolev.githubrepos.services.*

val serviceModule = module {
    val isDebug = BuildConfig.DEBUG
    single<ApiService> { ApiServiceImpl(isDebug, get()) }
    single<LoggingService> { LoggingServiceImpl(isDebug) }
    factory { ImageLoader(androidContext()) }
    factory { ServerUserMapper() }
    factory { ServerRepositoryMapper() }
    factory<ApiExceptionMapper> { ApiExceptionMapperImpl() }
}