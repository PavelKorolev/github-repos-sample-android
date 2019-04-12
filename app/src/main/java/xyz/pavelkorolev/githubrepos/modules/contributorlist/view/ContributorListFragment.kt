package xyz.pavelkorolev.githubrepos.modules.contributorlist.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.githubrepos.entities.ErrorState
import xyz.pavelkorolev.githubrepos.helpers.*
import xyz.pavelkorolev.githubrepos.modules.base.BaseFragment
import xyz.pavelkorolev.githubrepos.modules.base.BaseIntent
import xyz.pavelkorolev.githubrepos.modules.base.BaseView
import xyz.pavelkorolev.githubrepos.modules.base.NavigationMode
import xyz.pavelkorolev.githubrepos.modules.contributorlist.ContributorListViewModel
import xyz.pavelkorolev.githubrepos.modules.contributorlist.ContributorListViewState
import xyz.pavelkorolev.githubrepos.modules.contributorlist.di.ContributorListModule
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import javax.inject.Inject

private const val ORGANIZATION_KEY = "organization"
private const val REPOSITORY_KEY = "repository"

sealed class ContributorListIntent : BaseIntent {
    data class InitialData(
        val organization: String,
        val repository: String
    ) : ContributorListIntent()

    object PullToRefresh : ContributorListIntent()
}

class ContributorListFragment : BaseFragment(), BaseView<ContributorListIntent, ContributorListViewState> {

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[ContributorListViewModel::class.java]
    }

    private lateinit var recycler: RecyclerView
    private lateinit var refresher: SwipeRefreshLayout
    private lateinit var emptyView: View
    private lateinit var errorTextView: TextView

    private val controller = ContributorListController()

    private val component by lazy {
        app.component.plus(ContributorListModule(this))
    }

    private lateinit var organization: String
    private lateinit var repository: String

    override fun findViews() {
        super.findViews()
        recycler = find(R.id.recycler)
        refresher = find<SwipeRefreshLayout>(R.id.refresher).apply {
            setColorSchemeResources(R.color.accent)
        }
        emptyView = find(R.id.empty_view)
        errorTextView = find(R.id.error_text_view)
    }

    override fun loadArguments() {
        super.loadArguments()
        organization = getArgumentString(ORGANIZATION_KEY)
        repository = getArgumentString(REPOSITORY_KEY)
    }

    override fun layoutResource() = R.layout.fragment_contributor_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component.inject(this)

        viewModel.stateUpdatesOn(schedulerProvider.main())
            .subscribe(::render)
            .addDisposableTo(disposable)
        viewModel.processIntents(intents())

        setupToolbar(getString(R.string.contributor_list, repository), NavigationMode.BACK)

        recycler.setAdapterFromController(controller)
        recycler.addDefaultSeparators()
    }

    override fun intents(): Observable<ContributorListIntent> = Observable.mergeArray(
        Observable.just(ContributorListIntent.InitialData(organization, repository)),
        refresher.refreshes().map { ContributorListIntent.PullToRefresh }
    )

    override fun render(state: ContributorListViewState) {
        emptyView.isVisible = state.contributorList?.isEmpty() ?: false
        when (state.errorState) {
            is ErrorState.Message -> {
                errorTextView.text = state.errorState.text
                errorTextView.isVisible = true
            }
            is ErrorState.None -> errorTextView.isVisible = false
        }
        refresher.isRefreshing = state.isLoading

        controller.contributorList = state.contributorList
        controller.requestModelBuild()
    }

    companion object {
        fun instance(organization: String, repository: String) = instanceOf<ContributorListFragment>().apply {
            arguments = bundleOf(
                ORGANIZATION_KEY to organization,
                REPOSITORY_KEY to repository
            )
        }
    }

}
