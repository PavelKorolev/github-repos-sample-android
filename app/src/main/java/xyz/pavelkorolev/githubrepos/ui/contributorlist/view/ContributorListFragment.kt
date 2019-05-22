package xyz.pavelkorolev.githubrepos.ui.contributorlist.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.Observable
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import xyz.pavelkorolev.githubrepos.ui.base.BaseFragment
import xyz.pavelkorolev.githubrepos.ui.base.BaseIntent
import xyz.pavelkorolev.githubrepos.ui.base.BaseView
import xyz.pavelkorolev.githubrepos.ui.base.NavigationMode
import xyz.pavelkorolev.githubrepos.ui.contributorlist.ContributorListViewModel
import xyz.pavelkorolev.githubrepos.ui.contributorlist.ContributorListViewState
import xyz.pavelkorolev.githubrepos.ui.contributorlist.di.injectContributorListModule
import xyz.pavelkorolev.githubrepos.utils.addDefaultSeparators
import xyz.pavelkorolev.githubrepos.utils.addDisposableTo
import xyz.pavelkorolev.githubrepos.utils.refreshes
import xyz.pavelkorolev.githubrepos.utils.setAdapterFromController
import xyz.pavelkorolev.helper.find
import xyz.pavelkorolev.helper.getArgumentString
import xyz.pavelkorolev.helper.instanceOf

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

    private val schedulerProvider: SchedulerProvider by inject()
    private val controller: ContributorListController by inject()
    private val contributorListViewModel: ContributorListViewModel by viewModel()

    private lateinit var recycler: RecyclerView
    private lateinit var refresher: SwipeRefreshLayout
    private lateinit var emptyView: View
    private lateinit var errorTextView: TextView

    private lateinit var organization: String
    private lateinit var repository: String

    override fun findViews() {
        super.findViews()
        recycler = find(R.id.recycler)
        refresher = find<SwipeRefreshLayout>(R.id.refresher).apply {
            setColorSchemeResources(R.color.color_primary)
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
        injectContributorListModule()

        contributorListViewModel.stateUpdatesOn(schedulerProvider.main())
            .subscribe(::render)
            .addDisposableTo(disposable)
        contributorListViewModel.processIntents(intents())

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
        errorTextView.isVisible = state.isError
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
