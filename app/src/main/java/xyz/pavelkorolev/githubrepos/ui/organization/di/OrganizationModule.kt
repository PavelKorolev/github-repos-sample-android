package xyz.pavelkorolev.githubrepos.ui.organization.di

import android.content.Context
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import xyz.pavelkorolev.githubrepos.ui.organization.OrganizationRouter
import xyz.pavelkorolev.githubrepos.ui.organization.OrganizationRouterImpl
import xyz.pavelkorolev.githubrepos.ui.organization.OrganizationViewModel

val organizationModule = module {
    factory<OrganizationRouter> { (context: Context?) -> OrganizationRouterImpl(get { parametersOf(context) }) }
    viewModel { OrganizationViewModel() }
}

val loadOrganizationModule by lazy { loadKoinModules(organizationModule) }
fun injectOrganizationModule() = loadOrganizationModule