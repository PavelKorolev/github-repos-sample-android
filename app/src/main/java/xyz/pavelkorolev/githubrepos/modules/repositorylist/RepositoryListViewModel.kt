package xyz.pavelkorolev.githubrepos.modules.repositorylist

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.entities.ErrorState
import xyz.pavelkorolev.githubrepos.entities.Repository
import xyz.pavelkorolev.githubrepos.helpers.*
import xyz.pavelkorolev.githubrepos.modules.base.BaseAction
import xyz.pavelkorolev.githubrepos.modules.base.BaseViewModel
import xyz.pavelkorolev.githubrepos.modules.base.BaseViewState
import xyz.pavelkorolev.githubrepos.modules.repositorylist.view.RepositoryListIntent
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import javax.inject.Inject

data class RepositoryListViewState(
    val repositoryList: List<Repository>? = null,
    val isLoading: Boolean = false,
    val errorState: ErrorState = ErrorState.None
) : BaseViewState

sealed class RepositoryListAction : BaseAction {
    data class UpdateRepositoryList(val repositoryList: List<Repository>) : RepositoryListAction()
    data class UpdateLoading(val isLoading: Boolean) : RepositoryListAction()
    data class UpdateErrorState(val errorState: ErrorState) : RepositoryListAction()
}

class RepositoryListViewModel @Inject constructor(
    private val interactor: RepositoryListInteractor,
    private val schedulerProvider: SchedulerProvider
) : BaseViewModel<RepositoryListIntent, RepositoryListAction, RepositoryListViewState>() {

    lateinit var router: RepositoryListRouter

    private val initialState = RepositoryListViewState()

    init {
        val intentsConnectable = intentRelay.publish()

        val initialData = intentsConnectable.ofType(RepositoryListIntent.InitialData::class.java)
            .take(1)

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
                    .subscribeOn(schedulerProvider.io())
                    .map<RepositoryListAction> { RepositoryListAction.UpdateRepositoryList(it) }
                    .onErrorReturn { RepositoryListAction.UpdateErrorState(ErrorState.Message("Loading Error")) }
                    .startWith(RepositoryListAction.UpdateLoading(true))
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
                errorState = ErrorState.None
            )
            is RepositoryListAction.UpdateLoading -> state.copy(
                isLoading = action.isLoading
            )
            is RepositoryListAction.UpdateErrorState -> state.copy(
                errorState = action.errorState,
                isLoading = false
            )
        }
    }

}
