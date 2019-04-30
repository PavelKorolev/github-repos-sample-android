package xyz.pavelkorolev.githubrepos.ui.organization.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xyz.pavelkorolev.githubrepos.R
import xyz.pavelkorolev.githubrepos.ui.main.MainActivity
import xyz.pavelkorolev.githubrepos.ui.organization.OrganizationViewState

class OrganizationFragmentTest {

    @Rule
    @JvmField
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var fragment: OrganizationFragment

    @Before
    fun before() {
        val contentFragmentManager = activityTestRule.activity.contentFragmentManager
        fragment = contentFragmentManager.fragments.last() as OrganizationFragment
    }

    @Test
    fun shouldRenderEnabledButton() {
        val state = OrganizationViewState(true)
        runOnUiThread {
            fragment.render(state)
        }
        onView(withId(R.id.button)).check(matches(isEnabled()))
    }

    @Test
    fun shouldRenderDisabledButton() {
        val state = OrganizationViewState(false)
        runOnUiThread {
            fragment.render(state)
        }
        onView(withId(R.id.button)).check(matches(not(isEnabled())))
    }

}
