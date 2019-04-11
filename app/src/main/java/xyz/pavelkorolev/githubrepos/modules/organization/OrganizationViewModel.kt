package xyz.pavelkorolev.githubrepos.modules.organization

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.helpers.addDisposableTo
import xyz.pavelkorolev.githubrepos.helpers.connectInto
import xyz.pavelkorolev.githubrepos.helpers.mapToLatestFrom
import xyz.pavelkorolev.githubrepos.modules.base.BaseAction
import xyz.pavelkorolev.githubrepos.modules.base.BaseViewModel
import xyz.pavelkorolev.githubrepos.modules.base.BaseViewState
import xyz.pavelkorolev.githubrepos.modules.organization.view.OrganizationIntent
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import javax.inject.Inject

data class OrganizationViewState(
    val isButtonEnabled: Boolean = false
) : BaseViewState

sealed class OrganizationAction : BaseAction {
    data class UpdateButtonEnabled(val isEnabled: Boolean) : OrganizationAction()
}

class OrganizationViewModel @Inject constructor(
    private val interactor: OrganizationInteractor,
    private val schedulerProvider: SchedulerProvider
) : BaseViewModel<OrganizationIntent, OrganizationAction, OrganizationViewState>() {

    lateinit var router: OrganizationRouter

    private val initialState = OrganizationViewState()

    init {
        val intentsConnectable = intentRelay.publish()

        val organizationTextChanges = intentsConnectable.ofType(OrganizationIntent.OrganizationTextChanges::class.java)
            .map { it.text }
            .publish()

        val buttonEnableActions = organizationTextChanges
            .map { !it.isEmpty() }
            .map { OrganizationAction.UpdateButtonEnabled(it) }

        intentsConnectable.ofType(OrganizationIntent.Open::class.java)
            .mapToLatestFrom(organizationTextChanges)
            .subscribe { organization ->
                router.openRepositoryList(organization)
            }
            .addDisposableTo(disposable)

        Observable
            .mergeArray(
                buttonEnableActions
            )
            .scan(initialState, ::reduce)
            .subscribe(stateRelay)
            .addDisposableTo(disposable)

        organizationTextChanges.connectInto(disposable)
        intentsConnectable.connectInto(disposable)
    }

    private fun reduce(state: OrganizationViewState, action: OrganizationAction): OrganizationViewState {
        return when (action) {
            is OrganizationAction.UpdateButtonEnabled -> state.copy(
                isButtonEnabled = action.isEnabled
            )
        }
    }

}
