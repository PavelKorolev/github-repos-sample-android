package xyz.pavelkorolev.githubrepos.modules.contributorlist

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.entities.User
import xyz.pavelkorolev.githubrepos.services.ApiService

interface ContributorListInteractor {
    fun loadContributorList(organization: String, repository: String): Observable<List<User>>
}

class ContributorListInteractorImpl(private val apiService: ApiService) : ContributorListInteractor {

    override fun loadContributorList(organization: String, repository: String): Observable<List<User>> =
        apiService.getContributors(organization, repository)
            .map { serverContributorList ->
                serverContributorList.map {
                    val id = it.id ?: throw RuntimeException("Server user must have id")
                    val login = it.login ?: throw RuntimeException("Server user must have login")
                    val avatarUrl = it.avatar_url
                    User(id, login, avatarUrl)
                }
            }

}
