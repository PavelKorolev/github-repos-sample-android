package xyz.pavelkorolev.githubrepos.ui.contributorlist

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import xyz.pavelkorolev.githubrepos.TestSchedulerProvider
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import xyz.pavelkorolev.githubrepos.ui.contributorlist.view.ContributorListIntent

object ContributorListViewModelSpec : Spek({
    describe("ContributorListViewModel") {
        val schedulerProvider: SchedulerProvider = TestSchedulerProvider()
        val interactor: ContributorListInteractor = mock()

        val viewModel by memoized {
            ContributorListViewModel(interactor, schedulerProvider)
        }

        whenever(interactor.loadContributorList(any(), any())).thenReturn(Observable.empty())
        lateinit var stateChanges: TestObserver<ContributorListViewState>
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
                whenever(interactor.loadContributorList(any(), any()))
                    .thenReturn(
                        Observable.just(
                            ContributorListLoadResult.Loading,
                            ContributorListLoadResult.Success(emptyList())
                        )
                    )
                viewModel.processIntents(Observable.just(ContributorListIntent.InitialData("google", "dagger")))
                // 0 - Initial state
                // 1 - Initial loading
                // 2 - Initial success
                stateChanges.assertValueAt(1) { it.isLoading }
                stateChanges.assertValueAt(2) { it.contributorList != null }
            }
            it("should start loading on pull to refresh") {
                whenever(interactor.loadContributorList(any(), any()))
                    .thenReturn(
                        Observable.just(
                            ContributorListLoadResult.Loading,
                            ContributorListLoadResult.Success(emptyList())
                        )
                    )
                viewModel.processIntents(Observable.just(ContributorListIntent.InitialData("google", "dagger")))
                viewModel.processIntents(Observable.just(ContributorListIntent.PullToRefresh))
                // 0 - Initial state
                // 1 - Initial loading
                // 2 - Initial success
                // 3 - Pull to refresh loading
                // 4 - Pull to refresh success
                stateChanges.assertValueAt(3) { it.isLoading }
                stateChanges.assertValueAt(4) { it.contributorList != null }
            }

            it("should handle error without throwing") {
                whenever(interactor.loadContributorList(any(), any())).thenReturn(
                    Observable.just(
                        ContributorListLoadResult.Error
                    )
                )
                viewModel.processIntents(Observable.just(ContributorListIntent.InitialData("google", "dagger")))
                stateChanges.assertValueAt(0) { !it.isError }
                stateChanges.assertValueAt(1) { it.isError }
                stateChanges.assertNoErrors()
            }

        }
    }
})
