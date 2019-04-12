package xyz.pavelkorolev.githubrepos.ui.organization.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import xyz.pavelkorolev.githubrepos.di.ViewModelKey
import xyz.pavelkorolev.githubrepos.ui.organization.OrganizationRouter
import xyz.pavelkorolev.githubrepos.ui.organization.OrganizationRouterImpl
import xyz.pavelkorolev.githubrepos.ui.organization.OrganizationViewModel
import xyz.pavelkorolev.githubrepos.ui.organization.view.OrganizationFragment
import xyz.pavelkorolev.githubrepos.services.Navigator
import xyz.pavelkorolev.githubrepos.services.NavigatorImpl

@Subcomponent(
    modules = [
        OrganizationModule::class,
        OrganizationPresentationModule::class
    ]
)
interface OrganizationComponent {
    fun inject(fragment: OrganizationFragment)
}

@Module
class OrganizationModule(private val fragment: OrganizationFragment) {

    @Provides
    fun provideNavigator(): Navigator = NavigatorImpl(fragment.activity)

    @Provides
    fun provideRouter(navigator: Navigator): OrganizationRouter = OrganizationRouterImpl(navigator)

}

@Module
abstract class OrganizationPresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(OrganizationViewModel::class)
    abstract fun bindViewModel(viewModel: OrganizationViewModel): ViewModel

}
