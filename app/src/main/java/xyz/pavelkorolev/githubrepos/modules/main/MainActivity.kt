package xyz.pavelkorolev.githubrepos.modules.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import xyz.pavelkorolev.githubrepos.modules.base.BaseFragmentActivity
import xyz.pavelkorolev.githubrepos.modules.repositorylist.RepositoryListFragment

class MainActivity : BaseFragmentActivity() {

    override fun rootFragment(): Fragment = RepositoryListFragment.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        fun startIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

}
