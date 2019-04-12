package xyz.pavelkorolev.githubrepos.ui.repositorylist

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.models.Repository
import xyz.pavelkorolev.githubrepos.services.ApiService

interface RepositoryListInteractor {
    fun loadRepositoryList(organization: String): Observable<List<Repository>>
}

class RepositoryListInteractorImpl(private val apiService: ApiService) : RepositoryListInteractor {

    override fun loadRepositoryList(organization: String): Observable<List<Repository>> =
        apiService.getRepositories(organization)
            .map { serverRepositoryList ->
                serverRepositoryList.map {
                    val id = it.id ?: throw RuntimeException("Server repository must have id")
                    val title = it.name ?: throw RuntimeException("Server repository must have title")
                    val url = it.html_url ?: throw RuntimeException("Server repository must have url")
                    val description = it.description
                    Repository(id, title, description, url)
                }
            }

}
