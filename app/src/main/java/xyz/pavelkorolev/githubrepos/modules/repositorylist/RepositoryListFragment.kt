package xyz.pavelkorolev.githubrepos.modules.repositorylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.githubrepos.helpers.addDisposableTo
import xyz.pavelkorolev.githubrepos.helpers.app
import xyz.pavelkorolev.githubrepos.helpers.find
import xyz.pavelkorolev.githubrepos.helpers.instanceOf
import xyz.pavelkorolev.githubrepos.modules.base.BaseFragment
import xyz.pavelkorolev.githubrepos.modules.base.BaseIntent
import xyz.pavelkorolev.githubrepos.modules.base.BaseView
import xyz.pavelkorolev.githubrepos.modules.base.NavigationMode
import xyz.pavelkorolev.githubrepos.modules.repositorylist.di.RepositoryListModule
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider
import javax.inject.Inject

sealed class RepositoryListIntent : BaseIntent

class RepositoryListFragment : BaseFragment(), BaseView<RepositoryListIntent, RepositoryListViewState> {

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[RepositoryListViewModel::class.java]
    }

    private lateinit var recycler: RecyclerView
    private lateinit var emptyView: View

    private val component by lazy {
        app.component.plus(RepositoryListModule(this))
    }

    override fun findViews() {
        super.findViews()
        recycler = find(R.id.recycler)
        emptyView = find(R.id.empty_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_repository_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component.inject(this)

        with(viewModel) {
            stateUpdatesOn(schedulerProvider.main())
                .subscribe(::render)
                .addDisposableTo(disposable)
            processIntents(intents())
        }

        setupToolbar(R.string.repository_list, NavigationMode.NONE)
    }

    override fun intents(): Observable<RepositoryListIntent> = Observable.empty()

    override fun render(state: RepositoryListViewState) {
        emptyView.isVisible = state.repositoryList?.isEmpty() ?: false
    }

    companion object {
        fun instance() = instanceOf<RepositoryListFragment>()
    }

}
