package xyz.pavelkorolev.githubrepos.ui.repositorylist

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import xyz.pavelkorolev.githubrepos.TestSchedulerProvider
import xyz.pavelkorolev.githubrepos.models.Repository
import xyz.pavelkorolev.githubrepos.ui.repositorylist.view.RepositoryListIntent

object RepositoryListViewModelSpec : Spek({
    describe("RepositoryListViewModel") {
        val schedulerProvider = TestSchedulerProvider()
        val interactor: RepositoryListInteractor = mock()
        val router: RepositoryListRouter = mock()

        val viewModel by memoized {
            RepositoryListViewModel(interactor, schedulerProvider).apply {
                this.router = router
            }
        }

        describe("state updates") {
            lateinit var stateChanges: TestObserver<RepositoryListViewState>
            beforeEach {
                stateChanges = viewModel.stateUpdatesOn(schedulerProvider.main()).test()
            }

            describe("depending on interactor") {
                whenever(interactor.loadRepositoryList("google")).thenReturn(
                    Observable.just(
                        listOf(
                            Repository(1, "dagger", null, "https://github.com/google/dagger"),
                            Repository(
                                2,
                                "material-design-icons",
                                null,
                                "https://github.com/google/material-design-icons"
                            )
                        )
                    )
                )

                describe("with initial data") {
                    beforeEach {
                        viewModel.processIntents(Observable.just(RepositoryListIntent.InitialData("google")))
                    }

                    it("should start loading immediately after initial value") {
                        stateChanges.assertValueAt(1) { it.isLoading }
                    }

                    it("should start loading immediately after pull to refresh") {
                        stateChanges.assertValueAt(1) { it.isLoading }
                        stateChanges.assertValueAt(2) { it.repositoryList != null && !it.isLoading }
                        viewModel.processIntents(Observable.just(RepositoryListIntent.PullToRefresh))
                        stateChanges.assertValueAt(3) { it.isLoading }
                        stateChanges.assertValueAt(4) { it.repositoryList != null && !it.isLoading }
                    }
                }

            }

            it("should update repositories from interactor data") {
                val repositories = listOf(
                    Repository(1, "dagger", null, "https://github.com/google/dagger"),
                    Repository(2, "material-design-icons", null, "https://github.com/google/material-design-icons")
                )
                whenever(interactor.loadRepositoryList("google")).thenReturn(Observable.just(repositories))
                viewModel.processIntents(Observable.just(RepositoryListIntent.InitialData("google")))
                stateChanges.assertValueAt(2) { it.repositoryList == repositories }
            }

            it("should error if interactor failed to load data") {
                whenever(interactor.loadRepositoryList("google")).thenReturn(Observable.error(RuntimeException()))
                viewModel.processIntents(Observable.just(RepositoryListIntent.InitialData("google")))
                stateChanges.assertValueAt(2) { it.errorMessage != null }
                stateChanges.assertNoErrors()
            }

            afterEach {
                stateChanges.dispose()
            }
        }

        it("should call router with organization and repository provided on click") {
            val repository = Repository(1, "dagger", null, "https://github.com/google/dagger")
            viewModel.processIntents(
                Observable.just(
                    RepositoryListIntent.InitialData("google"),
                    RepositoryListIntent.RepositoryClick(repository)
                )
            )
            verify(router).openContributorList("google", "dagger")
        }
    }
})
