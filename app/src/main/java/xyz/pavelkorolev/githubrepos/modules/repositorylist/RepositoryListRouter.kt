package xyz.pavelkorolev.githubrepos.modules.repositorylist

import xyz.pavelkorolev.githubrepos.services.Navigator

interface RepositoryListRouter {
    fun openUrl(url: String)
}

class RepositoryListRouterImpl(private val navigator: Navigator) : RepositoryListRouter {

    override fun openUrl(url: String) {
        navigator.openUrl(url)
    }

}
