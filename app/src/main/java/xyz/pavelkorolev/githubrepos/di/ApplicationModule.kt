package xyz.pavelkorolev.githubrepos.di

import dagger.Component
import dagger.Module
import dagger.Provides
import xyz.pavelkorolev.githubrepos.application.App
import xyz.pavelkorolev.githubrepos.modules.base.BaseActivity
import xyz.pavelkorolev.githubrepos.modules.base.BaseFragment
import xyz.pavelkorolev.githubrepos.modules.repositorylist.di.RepositoryListComponent
import xyz.pavelkorolev.githubrepos.modules.repositorylist.di.RepositoryListModule
import xyz.pavelkorolev.githubrepos.modules.splash.SplashComponent
import xyz.pavelkorolev.githubrepos.modules.splash.SplashModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class,
    PresentationModule::class,
    ServiceModule::class,
    SchedulerModule::class])
interface AppComponent {
    fun inject(app: App)
    fun inject(activity: BaseActivity)
    fun inject(activity: BaseFragment)

    fun plus(module: SplashModule): SplashComponent
    fun plus(module: RepositoryListModule): RepositoryListComponent

}

@Module
class AppModule(private val app: App) {

    @Provides
    @Singleton
    fun provideApp() = app

}
