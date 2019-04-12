package xyz.pavelkorolev.githubrepos.ui.splash

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import xyz.pavelkorolev.githubrepos.application.Router
import xyz.pavelkorolev.githubrepos.application.RouterInput

@Subcomponent(modules = [SplashModule::class])
interface SplashComponent {
    fun inject(splashActivity: SplashActivity)
}

@Module
class SplashModule(private val splashActivity: SplashActivity) {

    @Provides
    fun providesContext(): Context = splashActivity

    @Provides
    fun providesRouter(): RouterInput = Router(splashActivity)

}