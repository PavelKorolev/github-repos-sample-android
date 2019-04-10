package xyz.pavelkorolev.githubrepos.modules.organization.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.githubrepos.helpers.addDisposableTo
import xyz.pavelkorolev.githubrepos.helpers.app
import xyz.pavelkorolev.githubrepos.helpers.find
import xyz.pavelkorolev.githubrepos.helpers.instanceOf
import xyz.pavelkorolev.githubrepos.modules.base.BaseFragment
import xyz.pavelkorolev.githubrepos.modules.base.BaseIntent
import xyz.pavelkorolev.githubrepos.modules.base.BaseView
import xyz.pavelkorolev.githubrepos.modules.organization.OrganizationRouter
import xyz.pavelkorolev.githubrepos.modules.organization.OrganizationViewModel
import xyz.pavelkorolev.githubrepos.modules.organization.OrganizationViewState
import xyz.pavelkorolev.githubrepos.modules.organization.di.OrganizationModule
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import javax.inject.Inject

sealed class OrganizationIntent : BaseIntent {
    data class OrganizationTextChanges(val text: String) : OrganizationIntent()
    object Open : OrganizationIntent()
}

class OrganizationFragment : BaseFragment(), BaseView<OrganizationIntent, OrganizationViewState> {

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    @Inject
    lateinit var organizationRouter: OrganizationRouter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[OrganizationViewModel::class.java]
    }

    private val component by lazy {
        app.component.plus(OrganizationModule(this))
    }

    lateinit var button: Button
    lateinit var editText: EditText

    override fun findViews() {
        super.findViews()
        button = find(R.id.button)
        editText = find(R.id.edit_text)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_organization, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component.inject(this)

        with(viewModel) {
            this.router = organizationRouter
            stateUpdatesOn(schedulerProvider.main())
                .subscribe(::render)
                .addDisposableTo(disposable)
            processIntents(intents())
        }

        editText.setText("google")
    }

    override fun intents(): Observable<OrganizationIntent> = Observable.mergeArray(
        editText.textChanges().map { OrganizationIntent.OrganizationTextChanges(it.toString()) },
        button.clicks().map { OrganizationIntent.Open }
    )

    override fun render(state: OrganizationViewState) {
        button.isEnabled = state.isButtonEnabled
    }

    companion object {
        fun instance() = instanceOf<OrganizationFragment>()
    }

}
