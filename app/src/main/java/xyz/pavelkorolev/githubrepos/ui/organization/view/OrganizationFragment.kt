package xyz.pavelkorolev.githubrepos.ui.organization.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.editorActions
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import xyz.pavelkorolev.githubrepos.BuildConfig
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import xyz.pavelkorolev.githubrepos.ui.base.BaseFragment
import xyz.pavelkorolev.githubrepos.ui.base.BaseIntent
import xyz.pavelkorolev.githubrepos.ui.base.BaseView
import xyz.pavelkorolev.githubrepos.ui.organization.OrganizationRouter
import xyz.pavelkorolev.githubrepos.ui.organization.OrganizationViewModel
import xyz.pavelkorolev.githubrepos.ui.organization.OrganizationViewState
import xyz.pavelkorolev.githubrepos.ui.organization.di.injectOrganizationModule
import xyz.pavelkorolev.githubrepos.utils.addDisposableTo
import xyz.pavelkorolev.helper.find
import xyz.pavelkorolev.helper.hideKeyboard
import xyz.pavelkorolev.helper.instanceOf

sealed class OrganizationIntent : BaseIntent {
    data class OrganizationTextChanges(val text: String) : OrganizationIntent()
    object Open : OrganizationIntent()
}

class OrganizationFragment : BaseFragment(), BaseView<OrganizationIntent, OrganizationViewState> {

    private val schedulerProvider: SchedulerProvider by inject()
    private val router: OrganizationRouter by inject { parametersOf(this.activity) }
    private val organizationViewModel: OrganizationViewModel by viewModel()

    private lateinit var button: Button
    private lateinit var editText: EditText

    override fun findViews() {
        super.findViews()
        button = find(R.id.button)
        editText = find(R.id.edit_text)
    }

    override fun layoutResource() = R.layout.fragment_organization

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectOrganizationModule()

        organizationViewModel.router = router
        organizationViewModel.stateUpdatesOn(schedulerProvider.main())
            .subscribe(::render)
            .addDisposableTo(disposable)
        organizationViewModel.processIntents(intents())

        if (BuildConfig.DEBUG) {
            editText.setText("google")
        }
    }

    override fun intents(): Observable<OrganizationIntent> = Observable.mergeArray(
        editText.textChanges().map { OrganizationIntent.OrganizationTextChanges(it.toString()) },
        editText.editorActions()
            .doOnNext { hideKeyboard() }
            .map { OrganizationIntent.Open },
        button.clicks().map { OrganizationIntent.Open }
    )

    override fun render(state: OrganizationViewState) {
        button.isEnabled = state.isButtonEnabled
    }

    companion object {
        fun instance() = instanceOf<OrganizationFragment>()
    }

}
