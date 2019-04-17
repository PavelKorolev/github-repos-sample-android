package xyz.pavelkorolev.githubrepos.ui.repositorylist

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.models.Repository
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import xyz.pavelkorolev.githubrepos.ui.base.BaseAction
import xyz.pavelkorolev.githubrepos.ui.base.BaseViewModel
import xyz.pavelkorolev.githubrepos.ui.base.BaseViewState
import xyz.pavelkorolev.githubrepos.ui.repositorylist.view.RepositoryListIntent
import xyz.pavelkorolev.githubrepos.utils.*
import javax.inject.Inject

data class RepositoryListViewState(
    val repositoryList: List<Repository>? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false
) : BaseViewState

sealed class RepositoryListAction : BaseAction {
    data class UpdateRepositoryList(val repositoryList: List<Repository>) : RepositoryListAction()
    object ShowLoading : RepositoryListAction()
    object ShowError : RepositoryListAction()
}

class RepositoryListViewModel @Inject constructor(
    private val interactor: RepositoryListInteractor,
    private val schedulerProvider: SchedulerProvider
) : BaseViewModel<RepositoryListIntent, RepositoryListAction, RepositoryListViewState>() {

    lateinit var router: RepositoryListRouter

    private val initialState = RepositoryListViewState()

    init {
        val intentsConnectable = intentRelay
            .logNext()
            .publish()

        val initialData = intentsConnectable.ofType(RepositoryListIntent.InitialData::class.java)
            .take(1)
            .share()

        val pullToRefreshes = intentsConnectable.ofType(RepositoryListIntent.PullToRefresh::class.java)
            .map { Unit }

        val startLoadIntents = initialData
            .map { it.organization }

        val repositoryListUpdateActions = Observable
            .mergeArray(
                startLoadIntents,
                pullToRefreshes.mapToLatestFrom(startLoadIntents)
            )
            .switchMap { organization ->
                interactor.loadRepositoryList(organization)
                    .map { result ->
                        when (result) {
                            is RepositoryListLoadResult.Success -> RepositoryListAction.UpdateRepositoryList(result.repositoryList)
                            is RepositoryListLoadResult.Error -> RepositoryListAction.ShowError
                            is RepositoryListLoadResult.Loading -> RepositoryListAction.ShowLoading
                        }
                    }
                    .subscribeOn(schedulerProvider.io())
            }

        intentsConnectable.ofType(RepositoryListIntent.RepositoryClick::class.java)
            .withLatestFrom(startLoadIntents) { repositoryClick, organization ->
                organization to repositoryClick.repository.title
            }
            .buttonThrottle()
            .subscribe { (organization, repository) ->
                router.openContributorList(organization, repository)
            }
            .addDisposableTo(disposable)

        Observable
            .mergeArray(
                repositoryListUpdateActions
            )
            .scan(initialState, ::reduce)
            .subscribe(stateRelay)
            .addDisposableTo(disposable)

        intentsConnectable.connectInto(disposable)
    }

    private fun reduce(state: RepositoryListViewState, action: RepositoryListAction): RepositoryListViewState {
        return when (action) {
            is RepositoryListAction.UpdateRepositoryList -> state.copy(
                repositoryList = action.repositoryList,
                isLoading = false,
                isError = false
            )
            is RepositoryListAction.ShowLoading -> state.copy(
                isLoading = true
            )
            is RepositoryListAction.ShowError -> state.copy(
                repositoryList = null,
                isError = true,
                isLoading = false
            )
        }
    }

}
