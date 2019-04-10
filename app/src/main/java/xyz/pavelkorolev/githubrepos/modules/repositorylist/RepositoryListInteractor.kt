package xyz.pavelkorolev.githubrepos.modules.repositorylist

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.entities.Repository
import xyz.pavelkorolev.githubrepos.services.ApiService

interface RepositoryListInteractor {
    fun loadRepositoryList(organization: String): Observable<List<Repository>>
}

class RepositoryListInteractorImpl(private val apiService: ApiService) : RepositoryListInteractor {

    override fun loadRepositoryList(organization: String): Observable<List<Repository>> =
        apiService.getRepositories(organization)
            .map { serverRepositoryList ->
                serverRepositoryList.map {
                    val name = it.name ?: throw RuntimeException("Server repository must have title")
                    Repository(name)
                }
            }

}
