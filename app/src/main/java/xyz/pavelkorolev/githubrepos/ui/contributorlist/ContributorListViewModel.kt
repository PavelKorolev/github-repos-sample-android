package xyz.pavelkorolev.githubrepos.ui.contributorlist

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.models.User
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import xyz.pavelkorolev.githubrepos.ui.base.BaseAction
import xyz.pavelkorolev.githubrepos.ui.base.BaseViewModel
import xyz.pavelkorolev.githubrepos.ui.base.BaseViewState
import xyz.pavelkorolev.githubrepos.ui.contributorlist.view.ContributorListIntent
import xyz.pavelkorolev.githubrepos.utils.addDisposableTo
import xyz.pavelkorolev.githubrepos.utils.connectInto
import xyz.pavelkorolev.githubrepos.utils.mapToLatestFrom
import javax.inject.Inject

data class ContributorListViewState(
    val contributorList: List<User>? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false
) : BaseViewState

sealed class ContributorListAction : BaseAction {
    data class UpdateContributorList(val contributorList: List<User>) : ContributorListAction()
    object ShowLoading : ContributorListAction()
    object ShowError : ContributorListAction()
}

class ContributorListViewModel @Inject constructor(
    private val interactor: ContributorListInteractor,
    private val schedulerProvider: SchedulerProvider
) : BaseViewModel<ContributorListIntent, ContributorListAction, ContributorListViewState>() {

    private val initialState = ContributorListViewState()

    init {
        val intentsConnectable = intentRelay.publish()

        val initialData = intentsConnectable.ofType(ContributorListIntent.InitialData::class.java)
            .take(1)

        val pullToRefreshes = intentsConnectable.ofType(ContributorListIntent.PullToRefresh::class.java)
            .map { Unit }

        val startLoadIntents = initialData.map { it.organization to it.repository }

        val contributorListUpdateActions = Observable
            .mergeArray(
                startLoadIntents,
                pullToRefreshes.mapToLatestFrom(startLoadIntents)
            )
            .switchMap { (organization, repository) ->
                interactor.loadContributorList(organization, repository)
                    .map { result ->
                        when (result) {
                            is ContributorListLoadResult.Success -> ContributorListAction.UpdateContributorList(result.contributorList)
                            is ContributorListLoadResult.Error -> ContributorListAction.ShowError
                            is ContributorListLoadResult.Loading -> ContributorListAction.ShowLoading
                        }
                    }
                    .subscribeOn(schedulerProvider.io())
            }

        Observable
            .mergeArray(
                contributorListUpdateActions
            )
            .scan(initialState, ::reduce)
            .subscribe(stateRelay)
            .addDisposableTo(disposable)

        intentsConnectable.connectInto(disposable)
    }

    private fun reduce(state: ContributorListViewState, action: ContributorListAction): ContributorListViewState {
        return when (action) {
            is ContributorListAction.UpdateContributorList -> state.copy(
                contributorList = action.contributorList,
                isLoading = false,
                isError = false
            )
            is ContributorListAction.ShowLoading -> state.copy(
                isLoading = true
            )
            is ContributorListAction.ShowError -> state.copy(
                contributorList = null,
                isError = true,
                isLoading = false
            )
        }
    }

}
