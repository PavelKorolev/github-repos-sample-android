package xyz.pavelkorolev.githubrepos.modules.contributorlist

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.entities.ErrorState
import xyz.pavelkorolev.githubrepos.entities.User
import xyz.pavelkorolev.githubrepos.helpers.addDisposableTo
import xyz.pavelkorolev.githubrepos.helpers.connectInto
import xyz.pavelkorolev.githubrepos.helpers.mapToLatestFrom
import xyz.pavelkorolev.githubrepos.modules.base.BaseAction
import xyz.pavelkorolev.githubrepos.modules.base.BaseViewModel
import xyz.pavelkorolev.githubrepos.modules.base.BaseViewState
import xyz.pavelkorolev.githubrepos.modules.contributorlist.view.ContributorListIntent
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import javax.inject.Inject

data class ContributorListViewState(
    val contributorList: List<User>? = null,
    val isLoading: Boolean = false,
    val errorState: ErrorState = ErrorState.None
) : BaseViewState

sealed class ContributorListAction : BaseAction {
    data class UpdateContributorList(val contributorList: List<User>) : ContributorListAction()
    data class UpdateLoading(val isLoading: Boolean) : ContributorListAction()
    data class UpdateErrorState(val errorState: ErrorState) : ContributorListAction()
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
                    .subscribeOn(schedulerProvider.io())
                    .map<ContributorListAction> { ContributorListAction.UpdateContributorList(it) }
                    .onErrorReturn { ContributorListAction.UpdateErrorState(ErrorState.Message("Loading Error")) }
                    .startWith(ContributorListAction.UpdateLoading(true))
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
                errorState = ErrorState.None
            )
            is ContributorListAction.UpdateLoading -> state.copy(
                isLoading = action.isLoading
            )
            is ContributorListAction.UpdateErrorState -> state.copy(
                errorState = action.errorState,
                isLoading = false
            )
        }
    }

}
