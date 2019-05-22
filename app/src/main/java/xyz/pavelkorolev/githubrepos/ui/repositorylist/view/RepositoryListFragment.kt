package xyz.pavelkorolev.githubrepos.ui.repositorylist.view

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
import org.koin.core.parameter.parametersOf
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.githubrepos.models.Repository
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import xyz.pavelkorolev.githubrepos.ui.base.BaseFragment
import xyz.pavelkorolev.githubrepos.ui.base.BaseIntent
import xyz.pavelkorolev.githubrepos.ui.base.BaseView
import xyz.pavelkorolev.githubrepos.ui.base.NavigationMode
import xyz.pavelkorolev.githubrepos.ui.repositorylist.RepositoryListRouter
import xyz.pavelkorolev.githubrepos.ui.repositorylist.RepositoryListViewModel
import xyz.pavelkorolev.githubrepos.ui.repositorylist.RepositoryListViewState
import xyz.pavelkorolev.githubrepos.ui.repositorylist.di.injectRepositoryListModule
import xyz.pavelkorolev.githubrepos.utils.addDefaultSeparators
import xyz.pavelkorolev.githubrepos.utils.addDisposableTo
import xyz.pavelkorolev.githubrepos.utils.refreshes
import xyz.pavelkorolev.githubrepos.utils.setAdapterFromController
import xyz.pavelkorolev.helper.find
import xyz.pavelkorolev.helper.getArgumentString
import xyz.pavelkorolev.helper.instanceOf

private const val ORGANIZATION_KEY = "organization"

sealed class RepositoryListIntent : BaseIntent {
    data class InitialData(
        val organization: String
    ) : RepositoryListIntent()

    data class RepositoryClick(
        val repository: Repository
    ) : RepositoryListIntent()

    object PullToRefresh : RepositoryListIntent()
}

class RepositoryListFragment : BaseFragment(), BaseView<RepositoryListIntent, RepositoryListViewState> {

    private val schedulerProvider: SchedulerProvider by inject()
    private val controller: RepositoryListController by inject()
    private val router: RepositoryListRouter by inject { parametersOf(this.activity) }
    private val repositoryListViewModel: RepositoryListViewModel by viewModel()

    private lateinit var recycler: RecyclerView
    private lateinit var refresher: SwipeRefreshLayout
    private lateinit var emptyView: View
    private lateinit var errorTextView: TextView

    private lateinit var organization: String

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
    }

    override fun layoutResource() = R.layout.fragment_repository_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectRepositoryListModule()

        repositoryListViewModel.router = router
        repositoryListViewModel.stateUpdatesOn(schedulerProvider.main())
            .subscribe(::render)
            .addDisposableTo(disposable)
        repositoryListViewModel.processIntents(intents())

        setupToolbar(getString(R.string.repository_list, organization), NavigationMode.BACK)

        recycler.setAdapterFromController(controller)
        recycler.addDefaultSeparators()
    }

    override fun intents(): Observable<RepositoryListIntent> = Observable.mergeArray(
        Observable.just(RepositoryListIntent.InitialData(organization)),
        controller.repositoryClicks().map { RepositoryListIntent.RepositoryClick(it) },
        refresher.refreshes().map { RepositoryListIntent.PullToRefresh }
    )

    override fun render(state: RepositoryListViewState) {
        emptyView.isVisible = state.repositoryList?.isEmpty() ?: false
        errorTextView.isVisible = state.isError
        refresher.isRefreshing = state.isLoading

        controller.repositoryList = state.repositoryList
        controller.requestModelBuild()
    }

    companion object {
        fun instance(organization: String) = instanceOf<RepositoryListFragment>().apply {
            arguments = bundleOf(
                ORGANIZATION_KEY to organization
            )
        }
    }

}
