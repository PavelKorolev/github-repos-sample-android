package xyz.pavelkorolev.githubrepos.ui.contributorlist.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import xyz.pavelkorolev.githubrepos.ui.contributorlist.ContributorListInteractor
import xyz.pavelkorolev.githubrepos.ui.contributorlist.ContributorListInteractorImpl
import xyz.pavelkorolev.githubrepos.ui.contributorlist.ContributorListViewModel
import xyz.pavelkorolev.githubrepos.ui.contributorlist.view.ContributorListController

val contributorListModule = module {
    factory { ContributorListController(get()) }
    factory<ContributorListInteractor> { ContributorListInteractorImpl(get(), get()) }
    viewModel { ContributorListViewModel(get(), get()) }
}

val loadContributorListModule by lazy { loadKoinModules(contributorListModule) }
fun injectContributorListModule() = loadContributorListModule
