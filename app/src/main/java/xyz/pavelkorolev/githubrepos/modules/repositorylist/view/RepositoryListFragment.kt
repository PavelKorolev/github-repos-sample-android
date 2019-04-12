package xyz.pavelkorolev.githubrepos.modules.repositorylist.view

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
import xyz.pavelkorolev.githubrepos.entities.Repository
import xyz.pavelkorolev.githubrepos.helpers.*
import xyz.pavelkorolev.githubrepos.modules.base.BaseFragment
import xyz.pavelkorolev.githubrepos.modules.base.BaseIntent
import xyz.pavelkorolev.githubrepos.modules.base.BaseView
import xyz.pavelkorolev.githubrepos.modules.base.NavigationMode
import xyz.pavelkorolev.githubrepos.modules.repositorylist.RepositoryListRouter
import xyz.pavelkorolev.githubrepos.modules.repositorylist.RepositoryListViewModel
import xyz.pavelkorolev.githubrepos.modules.repositorylist.RepositoryListViewState
import xyz.pavelkorolev.githubrepos.modules.repositorylist.di.RepositoryListModule
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import javax.inject.Inject

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

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    @Inject
    lateinit var router: RepositoryListRouter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[RepositoryListViewModel::class.java]
    }

    private lateinit var recycler: RecyclerView
    private lateinit var refresher: SwipeRefreshLayout
    private lateinit var emptyView: View
    private lateinit var errorTextView: TextView

    private val controller = RepositoryListController()

    private val component by lazy {
        app.component.plus(RepositoryListModule(this))
    }

    private lateinit var organization: String

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
    }

    override fun layoutResource() = R.layout.fragment_repository_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component.inject(this)

        viewModel.router = router
        viewModel.stateUpdatesOn(schedulerProvider.main())
            .subscribe(::render)
            .addDisposableTo(disposable)
        viewModel.processIntents(intents())

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
        when (state.errorState) {
            is ErrorState.Message -> {
                errorTextView.text = state.errorState.text
                errorTextView.isVisible = true
            }
            is ErrorState.None -> errorTextView.isVisible = false
        }
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
