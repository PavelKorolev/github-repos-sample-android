package xyz.pavelkorolev.githubrepos.ui.contributorlist

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.models.User
import xyz.pavelkorolev.githubrepos.network.ApiService
import xyz.pavelkorolev.githubrepos.services.ServerUserMapper

interface ContributorListInteractor {
    fun loadContributorList(organization: String, repository: String): Observable<List<User>>
}

class ContributorListInteractorImpl(
    private val apiService: ApiService,
    private val serverUserMapper: ServerUserMapper
) : ContributorListInteractor {

    override fun loadContributorList(organization: String, repository: String): Observable<List<User>> =
        apiService.getContributors(organization, repository)
            .map { serverUserMapper.map(it) }

}
