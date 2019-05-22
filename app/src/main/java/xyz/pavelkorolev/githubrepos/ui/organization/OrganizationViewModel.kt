package xyz.pavelkorolev.githubrepos.ui.organization

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.ui.base.BaseAction
import xyz.pavelkorolev.githubrepos.ui.base.BaseViewModel
import xyz.pavelkorolev.githubrepos.ui.base.BaseViewState
import xyz.pavelkorolev.githubrepos.ui.organization.view.OrganizationIntent
import xyz.pavelkorolev.githubrepos.utils.addDisposableTo
import xyz.pavelkorolev.githubrepos.utils.buttonThrottle
import xyz.pavelkorolev.githubrepos.utils.connectInto
import xyz.pavelkorolev.githubrepos.utils.mapToLatestFrom

data class OrganizationViewState(
    val isButtonEnabled: Boolean = false
) : BaseViewState

sealed class OrganizationAction : BaseAction {
    data class UpdateButtonEnabled(val isEnabled: Boolean) : OrganizationAction()
}

class OrganizationViewModel : BaseViewModel<OrganizationIntent, OrganizationAction, OrganizationViewState>() {

    lateinit var router: OrganizationRouter

    private val initialState = OrganizationViewState()

    init {
        val intentsConnectable = intentRelay.publish()

        val organizationTextChanges = intentsConnectable.ofType(OrganizationIntent.OrganizationTextChanges::class.java)
            .map { it.text }
            .publish()

        val buttonEnableActions = organizationTextChanges
            .map { it.isNotEmpty() }
            .map { OrganizationAction.UpdateButtonEnabled(it) }

        intentsConnectable.ofType(OrganizationIntent.Open::class.java)
            .mapToLatestFrom(organizationTextChanges)
            .buttonThrottle()
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
