package xyz.pavelkorolev.githubrepos.application

import android.app.Activity
import xyz.pavelkorolev.githubrepos.modules.main.MainActivity

interface RouterInput {
    fun start()
}

class Router(
    private val activity: Activity
) : RouterInput {

    override fun start() {
        activity.startActivity(MainActivity.startIntent(activity))
        activity.finish()
    }

}
