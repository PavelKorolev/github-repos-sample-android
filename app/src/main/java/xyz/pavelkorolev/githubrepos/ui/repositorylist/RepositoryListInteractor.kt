package xyz.pavelkorolev.githubrepos.ui.repositorylist

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.models.Repository
import xyz.pavelkorolev.githubrepos.network.ApiService
import xyz.pavelkorolev.githubrepos.services.ServerRepositoryMapper

sealed class RepositoryListLoadResult {
    data class Success(val repositoryList: List<Repository>) : RepositoryListLoadResult()
    object Error : RepositoryListLoadResult()
    object Loading : RepositoryListLoadResult()
}

interface RepositoryListInteractor {
    fun loadRepositoryList(organization: String): Observable<RepositoryListLoadResult>
}

class RepositoryListInteractorImpl(
    private val apiService: ApiService,
    private val repositoryMapper: ServerRepositoryMapper
) : RepositoryListInteractor {

    override fun loadRepositoryList(organization: String): Observable<RepositoryListLoadResult> =
        apiService.getRepositories(organization)
            .map { repositoryMapper.map(it) }
            .map<RepositoryListLoadResult> { RepositoryListLoadResult.Success(it) }
            .onErrorReturn { RepositoryListLoadResult.Error }
            .startWith(RepositoryListLoadResult.Loading)

}
