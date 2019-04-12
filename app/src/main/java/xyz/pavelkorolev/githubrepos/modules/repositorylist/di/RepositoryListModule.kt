package xyz.pavelkorolev.githubrepos.modules.repositorylist.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import xyz.pavelkorolev.githubrepos.di.ViewModelKey
import xyz.pavelkorolev.githubrepos.modules.repositorylist.*
import xyz.pavelkorolev.githubrepos.modules.repositorylist.view.RepositoryListController
import xyz.pavelkorolev.githubrepos.modules.repositorylist.view.RepositoryListFragment
import xyz.pavelkorolev.githubrepos.services.ApiService
import xyz.pavelkorolev.githubrepos.services.Navigator
import xyz.pavelkorolev.githubrepos.services.NavigatorImpl

@Subcomponent(
    modules = [
        RepositoryListModule::class,
        RepositoryListPresentationModule::class
    ]
)
interface RepositoryListComponent {
    fun inject(fragment: RepositoryListFragment)
}

@Module
class RepositoryListModule(private val fragment: RepositoryListFragment) {

    @Provides
    fun provideController(): RepositoryListController = RepositoryListController()

    @Provides
    fun provideInteractor(apiService: ApiService): RepositoryListInteractor = RepositoryListInteractorImpl(apiService)

    @Provides
    fun provideNavigator(): Navigator = NavigatorImpl(fragment.activity)

    @Provides
    fun provideRouter(navigator: Navigator): RepositoryListRouter = RepositoryListRouterImpl(navigator)

}

@Module
abstract class RepositoryListPresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(RepositoryListViewModel::class)
    abstract fun bindViewModel(viewModel: RepositoryListViewModel): ViewModel

}
