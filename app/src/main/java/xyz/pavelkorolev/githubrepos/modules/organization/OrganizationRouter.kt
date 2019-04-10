package xyz.pavelkorolev.githubrepos.modules.organization

import xyz.pavelkorolev.githubrepos.services.Navigator

interface OrganizationRouter {
    fun openRepositoryList(organization: String)
}

class OrganizationRouterImpl(private val navigator: Navigator) : OrganizationRouter {

    override fun openRepositoryList(organization: String) {
        navigator.pushRepositoryList(organization)
    }

}
