package xyz.pavelkorolev.githubrepos.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentManager
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.githubrepos.ui.base.BaseFragment
import xyz.pavelkorolev.githubrepos.ui.contributorlist.view.ContributorListFragment
import xyz.pavelkorolev.githubrepos.ui.main.MainActivity
import xyz.pavelkorolev.githubrepos.ui.main.NavigationRoot
import xyz.pavelkorolev.githubrepos.ui.organization.view.OrganizationFragment
import xyz.pavelkorolev.githubrepos.ui.repositorylist.view.RepositoryListFragment
import xyz.pavelkorolev.helper.hideKeyboard
import xyz.pavelkorolev.helper.transaction

interface Navigator {
    fun back(force: Boolean = false)

    fun openMain()
    fun rootOrganization()
    fun pushRepositoryList(organization: String)
    fun pushContributorList(organization: String, repository: String)

    fun openUrl(url: String)
}

class NavigatorImpl(
    private val context: Context?
) : Navigator {

    private val root = context as? NavigationRoot

    private val fragmentManager = root?.contentFragmentManager

    private fun hideKeyboard() {
        fragmentManager?.fragments?.lastOrNull()?.hideKeyboard()
    }

    private fun setRoot(fragment: BaseFragment) {
        hideKeyboard()
        popAll()
        fragmentManager?.transaction {
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }

    private fun push(fragment: BaseFragment) {
        hideKeyboard()
        fragmentManager?.transaction {
            setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            add(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }

    private fun pop() {
        hideKeyboard()
        fragmentManager?.popBackStack()
    }

    private fun popAll() {
        hideKeyboard()
        fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun back(force: Boolean) {
        hideKeyboard()
        if (fragmentManager == null) return
        val topFragment = fragmentManager.fragments.lastOrNull() as? BaseFragment
        if (topFragment == null) {
            root?.finish()
            return
        }
        val shouldContinue = topFragment.onBackPressed() || force
        if (!shouldContinue) return
        if (fragmentManager.backStackEntryCount > 1) {
            pop()
        } else {
            root?.finish()
        }
    }

    override fun openMain() {
        if (context == null) return
        val intent = MainActivity.startIntent(context)
        context.startActivity(intent)
    }

    override fun rootOrganization() {
        val fragment = OrganizationFragment.instance()
        setRoot(fragment)
    }

    override fun pushRepositoryList(organization: String) {
        val fragment = RepositoryListFragment.instance(organization)
        push(fragment)
    }

    override fun pushContributorList(organization: String, repository: String) {
        val fragment = ContributorListFragment.instance(organization, repository)
        push(fragment)
    }

    override fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context?.startActivity(intent)
    }

}
