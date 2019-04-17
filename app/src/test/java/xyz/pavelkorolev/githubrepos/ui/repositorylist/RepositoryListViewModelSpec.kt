package xyz.pavelkorolev.githubrepos.ui.repositorylist

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import xyz.pavelkorolev.githubrepos.TestSchedulerProvider
import xyz.pavelkorolev.githubrepos.models.Repository
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import xyz.pavelkorolev.githubrepos.ui.repositorylist.view.RepositoryListIntent

object RepositoryListViewModelSpec : Spek({
    describe("RepositoryListViewModel") {
        val schedulerProvider: SchedulerProvider = TestSchedulerProvider()
        val interactor: RepositoryListInteractor = mock()
        val router: RepositoryListRouter = mock()

        val viewModel by memoized {
            RepositoryListViewModel(interactor, schedulerProvider).apply {
                this.router = router
            }
        }

        it("should call router with organization and repository provided") {
            val repository = Repository(1, "dagger", null, "https://github.com/google/dagger")
            viewModel.processIntents(
                Observable.just(
                    RepositoryListIntent.InitialData("google"),
                    RepositoryListIntent.RepositoryClick(repository)
                )
            )
            verify(router).openContributorList("google", "dagger")
        }

        whenever(interactor.loadRepositoryList(any())).thenReturn(Observable.empty())
        lateinit var stateChanges: TestObserver<RepositoryListViewState>
        beforeEach {
            stateChanges = viewModel.stateUpdatesOn(schedulerProvider.main()).test()
        }
        afterEach {
            stateChanges.dispose()
        }

        it("should do nothing if initial data not provided") {
            // Only initial state is emitted
            stateChanges.assertValueCount(1)
        }

        describe("on initial data provided") {
            it("should start loading on initial data provided") {
                whenever(interactor.loadRepositoryList(any()))
                    .thenReturn(
                        Observable.just(
                            RepositoryListLoadResult.Loading,
                            RepositoryListLoadResult.Success(emptyList())
                        )
                    )
                viewModel.processIntents(Observable.just(RepositoryListIntent.InitialData("google")))
                // 0 - Initial state
                // 1 - Initial loading
                // 2 - Initial success
                stateChanges.assertValueAt(1) { it.isLoading }
                stateChanges.assertValueAt(2) { it.repositoryList != null }
            }
            it("should start loading on pull to refresh") {
                whenever(interactor.loadRepositoryList(any()))
                    .thenReturn(
                        Observable.just(
                            RepositoryListLoadResult.Loading,
                            RepositoryListLoadResult.Success(emptyList())
                        )
                    )
                viewModel.processIntents(Observable.just(RepositoryListIntent.InitialData("google")))
                viewModel.processIntents(Observable.just(RepositoryListIntent.PullToRefresh))
                // 0 - Initial state
                // 1 - Initial loading
                // 2 - Initial success
                // 3 - Pull to refresh loading
                // 4 - Pull to refresh success
                stateChanges.assertValueAt(3) { it.isLoading }
                stateChanges.assertValueAt(4) { it.repositoryList != null }
            }

            it("should handle error without throwing") {
                whenever(interactor.loadRepositoryList(any())).thenReturn(Observable.just(RepositoryListLoadResult.Error))
                viewModel.processIntents(Observable.just(RepositoryListIntent.InitialData("google")))
                stateChanges.assertValueAt(0) { !it.isError }
                stateChanges.assertValueAt(1) { it.isError }
                stateChanges.assertNoErrors()
            }

        }
    }
})
