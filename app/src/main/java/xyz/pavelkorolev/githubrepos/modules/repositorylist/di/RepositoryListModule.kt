package xyz.pavelkorolev.githubrepos.modules.repositorylist.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import xyz.pavelkorolev.githubrepos.di.ViewModelKey
import xyz.pavelkorolev.githubrepos.modules.repositorylist.RepositoryListInteractor
import xyz.pavelkorolev.githubrepos.modules.repositorylist.RepositoryListInteractorImpl
import xyz.pavelkorolev.githubrepos.modules.repositorylist.RepositoryListViewModel
import xyz.pavelkorolev.githubrepos.modules.repositorylist.view.RepositoryListFragment
import xyz.pavelkorolev.githubrepos.services.ApiService

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
    fun provideInteractor(apiService: ApiService): RepositoryListInteractor = RepositoryListInteractorImpl(apiService)

}

@Module
abstract class RepositoryListPresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(RepositoryListViewModel::class)
    abstract fun bindViewModel(viewModel: RepositoryListViewModel): ViewModel

}
