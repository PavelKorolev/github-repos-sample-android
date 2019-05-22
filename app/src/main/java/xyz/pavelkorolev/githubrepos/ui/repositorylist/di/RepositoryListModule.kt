package xyz.pavelkorolev.githubrepos.ui.repositorylist.di

import android.content.Context
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import xyz.pavelkorolev.githubrepos.ui.repositorylist.*
import xyz.pavelkorolev.githubrepos.ui.repositorylist.view.RepositoryListController

val repositoryListModule = module {
    factory { RepositoryListController() }
    factory<RepositoryListInteractor> { RepositoryListInteractorImpl(get(), get()) }
    factory<RepositoryListRouter> { (context: Context?) -> RepositoryListRouterImpl(get { parametersOf(context) }) }
    viewModel { RepositoryListViewModel(get(), get()) }
}

val loadRepositoryListModule by lazy { loadKoinModules(repositoryListModule) }
fun injectRepositoryListModule() = loadRepositoryListModule
