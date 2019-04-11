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
import com.jakewharton.rxbinding2.widget.editorActions
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.BuildConfig
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.githubrepos.helpers.*
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
    lateinit var router: OrganizationRouter

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

        viewModel.router = router
        viewModel.stateUpdatesOn(schedulerProvider.main())
            .subscribe(::render)
            .addDisposableTo(disposable)
        viewModel.processIntents(intents())

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
