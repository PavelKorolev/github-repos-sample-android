package xyz.pavelkorolev.githubrepos.di

import dagger.Component
import dagger.Module
import dagger.Provides
import xyz.pavelkorolev.githubrepos.application.App
import xyz.pavelkorolev.githubrepos.ui.base.BaseActivity
import xyz.pavelkorolev.githubrepos.ui.base.BaseFragment
import xyz.pavelkorolev.githubrepos.ui.contributorlist.di.ContributorListComponent
import xyz.pavelkorolev.githubrepos.ui.contributorlist.di.ContributorListModule
import xyz.pavelkorolev.githubrepos.ui.main.MainComponent
import xyz.pavelkorolev.githubrepos.ui.main.MainModule
import xyz.pavelkorolev.githubrepos.ui.organization.di.OrganizationComponent
import xyz.pavelkorolev.githubrepos.ui.organization.di.OrganizationModule
import xyz.pavelkorolev.githubrepos.ui.repositorylist.di.RepositoryListComponent
import xyz.pavelkorolev.githubrepos.ui.repositorylist.di.RepositoryListModule
import xyz.pavelkorolev.githubrepos.ui.splash.SplashComponent
import xyz.pavelkorolev.githubrepos.ui.splash.SplashModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        PresentationModule::class,
        ServiceModule::class,
        SchedulerModule::class
    ]
)
interface AppComponent {
    fun inject(app: App)
    fun inject(activity: BaseActivity)
    fun inject(activity: BaseFragment)

    fun plus(module: SplashModule): SplashComponent
    fun plus(module: MainModule): MainComponent
    fun plus(module: RepositoryListModule): RepositoryListComponent
    fun plus(module: OrganizationModule): OrganizationComponent
    fun plus(module: ContributorListModule): ContributorListComponent
}

@Module
class AppModule(private val app: App) {

    @Provides
    @Singleton
    fun provideApp() = app

}
