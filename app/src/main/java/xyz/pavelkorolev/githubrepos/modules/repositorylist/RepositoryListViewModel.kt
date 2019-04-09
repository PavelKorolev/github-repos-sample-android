package xyz.pavelkorolev.githubrepos.modules.repositorylist

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.entities.Repository
import xyz.pavelkorolev.githubrepos.helpers.addDisposableTo
import xyz.pavelkorolev.githubrepos.helpers.connectInto
import xyz.pavelkorolev.githubrepos.modules.base.BaseAction
import xyz.pavelkorolev.githubrepos.modules.base.BaseViewModel
import xyz.pavelkorolev.githubrepos.modules.base.BaseViewState
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import javax.inject.Inject

data class RepositoryListViewState(
    val repositoryList: List<Repository>? = null
) : BaseViewState

sealed class RepositoryListAction : BaseAction {
    data class UpdateRepositoryList(val repositoryList: List<Repository>) : RepositoryListAction()
}

class RepositoryListViewModel @Inject constructor(
        private val interactor: RepositoryListInteractor,
        private val schedulerProvider: SchedulerProvider
) : BaseViewModel<RepositoryListIntent, RepositoryListAction, RepositoryListViewState>() {

    init {
        val initialState = RepositoryListViewState()
        actionSubject
                .scan(initialState, ::reduce)
                .subscribe(stateSubject)
                .addDisposableTo(viewModelLifetime)
    }

    override fun processIntents(intents: Observable<RepositoryListIntent>) {
        super.processIntents(intents)

        val intentsConnectable = intents.publish()

        val repositoryListUpdateActions = interactor.loadRepositoryList()
            .subscribeOn(schedulerProvider.io())
            .map { RepositoryListAction.UpdateRepositoryList(it) }

        Observable
                .mergeArray(
                    repositoryListUpdateActions
                )
                .subscribe(actionSubject)
                .addDisposableTo(uiLifetime)

        intentsConnectable.connectInto(uiLifetime)
    }

    private fun reduce(state: RepositoryListViewState, action: RepositoryListAction): RepositoryListViewState {
        return when (action) {
            is RepositoryListAction.UpdateRepositoryList -> state.copy(repositoryList = action.repositoryList)
        }
    }

}
