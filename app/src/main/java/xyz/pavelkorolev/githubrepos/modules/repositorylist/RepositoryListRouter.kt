package xyz.pavelkorolev.githubrepos.modules.repositorylist

import xyz.pavelkorolev.githubrepos.services.Navigator

interface RepositoryListRouter {
    fun openContributorList(organization: String, repository: String)
}

class RepositoryListRouterImpl(private val navigator: Navigator) : RepositoryListRouter {

    override fun openContributorList(organization: String, repository: String) {
        navigator.pushContributorList(organization, repository)
    }

}
