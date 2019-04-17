package xyz.pavelkorolev.githubrepos.ui.organization

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import xyz.pavelkorolev.githubrepos.TestSchedulerProvider
import xyz.pavelkorolev.githubrepos.ui.organization.view.OrganizationIntent

object OrganizationViewModelSpec : Spek({
    describe("OrganizationViewModel") {
        val schedulerProvider = TestSchedulerProvider()
        val router: OrganizationRouter = mock()

        val viewModel by memoized {
            OrganizationViewModel().apply {
                this.router = router
            }
        }

        describe("state updates") {
            lateinit var stateChanges: TestObserver<OrganizationViewState>
            beforeEach {
                stateChanges = viewModel.stateUpdatesOn(schedulerProvider.main()).test()
            }
            it("should have initial state on start") {
                val initialState = OrganizationViewState()
                stateChanges.assertValue(initialState)
            }
            it("should enable button depending on text changes") {
                viewModel.processIntents(Observable.just(OrganizationIntent.OrganizationTextChanges("a")))
                stateChanges.assertValueAt(1) { it.isButtonEnabled }
                viewModel.processIntents(Observable.just(OrganizationIntent.OrganizationTextChanges("")))
                stateChanges.assertValueAt(2) { !it.isButtonEnabled }
                viewModel.processIntents(Observable.just(OrganizationIntent.OrganizationTextChanges("b")))
                stateChanges.assertValueAt(3) { it.isButtonEnabled }
            }
            afterEach {
                stateChanges.dispose()
            }
        }

        it("should call router with organization provided") {
            viewModel.processIntents(
                Observable.just(
                    OrganizationIntent.OrganizationTextChanges("google"),
                    OrganizationIntent.Open
                )
            )
            verify(router).openRepositoryList("google")
        }
    }
})
