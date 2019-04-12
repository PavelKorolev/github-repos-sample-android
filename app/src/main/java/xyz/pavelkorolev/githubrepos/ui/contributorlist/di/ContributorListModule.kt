package xyz.pavelkorolev.githubrepos.ui.contributorlist.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import xyz.pavelkorolev.githubrepos.di.ViewModelKey
import xyz.pavelkorolev.githubrepos.ui.contributorlist.ContributorListInteractor
import xyz.pavelkorolev.githubrepos.ui.contributorlist.ContributorListInteractorImpl
import xyz.pavelkorolev.githubrepos.ui.contributorlist.ContributorListViewModel
import xyz.pavelkorolev.githubrepos.ui.contributorlist.view.ContributorListController
import xyz.pavelkorolev.githubrepos.ui.contributorlist.view.ContributorListFragment
import xyz.pavelkorolev.githubrepos.services.ApiService
import xyz.pavelkorolev.githubrepos.services.ImageLoader

@Subcomponent(
    modules = [
        ContributorListModule::class,
        ContributorListPresentationModule::class
    ]
)
interface ContributorListComponent {
    fun inject(fragment: ContributorListFragment)
}

@Module
class ContributorListModule(private val fragment: ContributorListFragment) {

    @Provides
    fun provideInteractor(apiService: ApiService): ContributorListInteractor = ContributorListInteractorImpl(apiService)

    @Provides
    fun provideController(imageLoader: ImageLoader): ContributorListController = ContributorListController(imageLoader)

}

@Module
abstract class ContributorListPresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(ContributorListViewModel::class)
    abstract fun bindViewModel(viewModel: ContributorListViewModel): ViewModel

}
