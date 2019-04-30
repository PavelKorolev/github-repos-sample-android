package xyz.pavelkorolev.githubrepos.ui.repositorylist

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import xyz.pavelkorolev.githubrepos.network.ApiService
import xyz.pavelkorolev.githubrepos.network.models.ServerRepository
import xyz.pavelkorolev.githubrepos.services.ServerRepositoryMapper

object RepositoryListInteractorSpec : Spek({
    describe("RepositoryListInteractor") {
        val apiService: ApiService = mock()
        val repositoryMapper = ServerRepositoryMapper()
        val interactor: RepositoryListInteractor by memoized {
            RepositoryListInteractorImpl(apiService, repositoryMapper)
        }
        describe("on repositories load") {
            describe("on success") {
                lateinit var repositoryLoadResults: TestObserver<RepositoryListLoadResult>
                beforeEach {
                    val mockServerRepositories = listOf(
                        ServerRepository(
                            1,
                            "spek",
                            "https://github.com/spekframework/spek",
                            "A specification framework for Kotlin"
                        ),
                        ServerRepository(
                            2,
                            "kotlin",
                            "https://github.com/JetBrains/kotlin",
                            "The Kotlin Programming Language"
                        )
                    )
                    whenever(apiService.getRepositories(any())).thenReturn(Observable.just(mockServerRepositories))
                    repositoryLoadResults = interactor.loadRepositoryList("google").test()
                }
                afterEach {
                    repositoryLoadResults.dispose()
                }

                it("should start with loading") {
                    repositoryLoadResults.assertValueAt(0) { it is RepositoryListLoadResult.Loading }
                }
                it("should return success and terminate") {
                    repositoryLoadResults.assertValueAt(1) { it is RepositoryListLoadResult.Success }
                    repositoryLoadResults.assertComplete()
                }
            }

            describe("on error") {
                lateinit var repositoryLoadResults: TestObserver<RepositoryListLoadResult>
                beforeEach {
                    whenever(apiService.getRepositories(any())).thenReturn(Observable.error(RuntimeException()))
                    repositoryLoadResults = interactor.loadRepositoryList("google").test()
                }
                afterEach {
                    repositoryLoadResults.dispose()
                }

                it("should start with loading") {
                    repositoryLoadResults.assertValueAt(0) { it is RepositoryListLoadResult.Loading }
                }
                it("should return error and terminate") {
                    repositoryLoadResults.assertValueAt(1) { it is RepositoryListLoadResult.Error }
                    repositoryLoadResults.assertComplete()
                }
            }
        }
    }
})
