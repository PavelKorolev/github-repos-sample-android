package xyz.pavelkorolev.githubrepos.ui.base

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.helper.compatColor
import xyz.pavelkorolev.helper.compatDrawable
import xyz.pavelkorolev.helper.tinted

enum class NavigationMode {
    MENU,
    BACK,
    NONE
}

abstract class BaseFragment : Fragment() {

    protected val disposable = CompositeDisposable()

    private var navigationMode: NavigationMode = NavigationMode.NONE

    private var toolbar: Toolbar? = null
    private var titleTextView: TextView? = null
    private var subtitleTextView: TextView? = null

    open fun loadArguments() = Unit

    abstract fun layoutResource(): Int

    open fun findViews() = Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutResource(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar) as? Toolbar
        titleTextView = view.findViewById(R.id.toolbar_title) as? TextView
        subtitleTextView = view.findViewById(R.id.toolbar_subtitle) as? TextView
        setSupportToolbar(toolbar)
        setHasOptionsMenu(true)
        loadArguments()
        findViews()
    }

    override fun onDestroyView() {
        disposable.clear()
        super.onDestroyView()
    }

    private fun getSupportActionBar(): ActionBar? {
        val compatActivity = activity as? AppCompatActivity
        return compatActivity?.supportActionBar
    }

    private fun setSupportToolbar(toolbar: Toolbar?) {
        val compatActivity = activity as? AppCompatActivity
        compatActivity?.setSupportActionBar(toolbar)
    }

    open fun onBackPressed() = true

    fun setupToolbar(
        title: String? = null,
        navigationMode: NavigationMode = NavigationMode.NONE
    ) {
        this.navigationMode = navigationMode
        setToolbarTitle(title)
        setToolbarSubtitle(null)

        if (navigationMode == NavigationMode.NONE) return

        val iconResource: Int
        val listenerFunction: () -> Unit

        when (navigationMode) {
            NavigationMode.MENU -> {
                iconResource = R.drawable.ic_menu
                listenerFunction = ::onNavigationMenuClick
            }
            NavigationMode.BACK -> {
                iconResource = R.drawable.ic_arrow_left
                listenerFunction = ::onNavigationBackClick
            }
            NavigationMode.NONE -> return
        }

        val tintColor = compatColor(R.color.toolbar_tint)
        val drawable = compatDrawable(iconResource)
        toolbar?.navigationIcon = drawable?.tinted(tintColor)
        toolbar?.setNavigationOnClickListener { listenerFunction() }
    }

    fun setToolbarTitle(title: String?) {
        val titleTextView = this.titleTextView
        if (titleTextView == null) {
            getSupportActionBar()?.title = title
        } else {
            titleTextView.text = title
        }
    }

    fun setToolbarSubtitle(subtitle: String?) {
        subtitleTextView?.isVisible = subtitle != null
        subtitleTextView?.text = subtitle
    }

    fun setupToolbar(
        @StringRes titleRes: Int,
        navigationMode: NavigationMode = NavigationMode.NONE
    ) {
        setupToolbar(getString(titleRes), navigationMode)
    }

    open fun onNavigationBackClick() {
        activity?.onBackPressed()
    }

    open fun onNavigationMenuClick() {
        // No operations.
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

}
