package xyz.pavelkorolev.githubrepos.ui.contributorlist

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.models.User
import xyz.pavelkorolev.githubrepos.network.ApiService
import xyz.pavelkorolev.githubrepos.services.ServerUserMapper

sealed class ContributorListLoadResult {
    data class Success(val contributorList: List<User>) : ContributorListLoadResult()
    object Error : ContributorListLoadResult()
    object Loading : ContributorListLoadResult()
}

interface ContributorListInteractor {
    fun loadContributorList(organization: String, repository: String): Observable<ContributorListLoadResult>
}

class ContributorListInteractorImpl(
    private val apiService: ApiService,
    private val serverUserMapper: ServerUserMapper
) : ContributorListInteractor {

    override fun loadContributorList(organization: String, repository: String): Observable<ContributorListLoadResult> =
        apiService.getContributors(organization, repository)
            .map { serverUserMapper.map(it) }
            .map<ContributorListLoadResult> { ContributorListLoadResult.Success(it) }
            .onErrorReturn { ContributorListLoadResult.Error }
            .startWith(ContributorListLoadResult.Loading)

}
