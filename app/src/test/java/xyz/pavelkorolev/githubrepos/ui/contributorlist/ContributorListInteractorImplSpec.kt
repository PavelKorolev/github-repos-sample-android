package xyz.pavelkorolev.githubrepos.ui.contributorlist

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import xyz.pavelkorolev.githubrepos.network.ApiService
import xyz.pavelkorolev.githubrepos.network.models.ServerUser
import xyz.pavelkorolev.githubrepos.services.ServerUserMapper

object ContributorListInteractorImplSpec : Spek({
    describe("ContributorListInteractor") {
        val apiService: ApiService = mock()
        val userMapper = ServerUserMapper()
        val interactor: ContributorListInteractor by memoized {
            ContributorListInteractorImpl(apiService, userMapper)
        }
        describe("on contributors load") {
            describe("on success") {
                lateinit var contributorListLoadResults: TestObserver<ContributorListLoadResult>
                beforeEach {
                    val mockServerUsers = listOf(
                        ServerUser(
                            1,
                            "PavelKorolev",
                            "https://avatars2.githubusercontent.com/u/4147260?s=460&v=4",
                            "https://github.com/PavelKorolev",
                            "pk@example.com"
                        ),
                        ServerUser(
                            2,
                            "JakeWharton",
                            "https://avatars1.githubusercontent.com/u/66577?s=460&v=4",
                            "https://github.com/JakeWharton",
                            "jw@example.com"
                        )
                    )

                    whenever(apiService.getContributors(any(), any())).thenReturn(Observable.just(mockServerUsers))
                    contributorListLoadResults = interactor.loadContributorList("google", "dagger").test()
                }
                afterEach {
                    contributorListLoadResults.dispose()
                }

                it("should start with loading") {
                    contributorListLoadResults.assertValueAt(0) { it is ContributorListLoadResult.Loading }
                }
                it("should return success and terminate") {
                    contributorListLoadResults.assertValueAt(1) { it is ContributorListLoadResult.Success }
                    contributorListLoadResults.assertComplete()
                }
            }

            describe("on error") {
                lateinit var contributorListLoadResults: TestObserver<ContributorListLoadResult>
                beforeEach {
                    whenever(apiService.getContributors(any(), any())).thenReturn(Observable.error(RuntimeException()))
                    contributorListLoadResults = interactor.loadContributorList("google", "dagger").test()
                }
                afterEach {
                    contributorListLoadResults.dispose()
                }

                it("should start with loading") {
                    contributorListLoadResults.assertValueAt(0) { it is ContributorListLoadResult.Loading }
                }
                it("should return error and terminate") {
                    contributorListLoadResults.assertValueAt(1) { it is ContributorListLoadResult.Error }
                    contributorListLoadResults.assertComplete()
                }
            }
        }
    }
})
