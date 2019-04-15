package xyz.pavelkorolev.githubrepos.ui.repositorylist

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.models.Repository
import xyz.pavelkorolev.githubrepos.network.ApiService
import xyz.pavelkorolev.githubrepos.services.ServerRepositoryMapper

interface RepositoryListInteractor {
    fun loadRepositoryList(organization: String): Observable<List<Repository>>
}

class RepositoryListInteractorImpl(
    private val apiService: ApiService,
    private val repositoryMapper: ServerRepositoryMapper
) : RepositoryListInteractor {

    override fun loadRepositoryList(organization: String): Observable<List<Repository>> =
        apiService.getRepositories(organization)
            .map { repositoryMapper.map(it) }

}
