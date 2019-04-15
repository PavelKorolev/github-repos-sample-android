package xyz.pavelkorolev.githubrepos.ui.repositorylist.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import xyz.pavelkorolev.githubrepos.di.ViewModelKey
import xyz.pavelkorolev.githubrepos.network.ApiService
import xyz.pavelkorolev.githubrepos.services.Navigator
import xyz.pavelkorolev.githubrepos.services.NavigatorImpl
import xyz.pavelkorolev.githubrepos.services.ServerRepositoryMapper
import xyz.pavelkorolev.githubrepos.ui.repositorylist.*
import xyz.pavelkorolev.githubrepos.ui.repositorylist.view.RepositoryListController
import xyz.pavelkorolev.githubrepos.ui.repositorylist.view.RepositoryListFragment

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
    fun provideInteractor(
        apiService: ApiService,
        repositoryMapper: ServerRepositoryMapper
    ): RepositoryListInteractor = RepositoryListInteractorImpl(apiService, repositoryMapper)

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
