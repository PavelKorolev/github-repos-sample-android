package xyz.pavelkorolev.githubrepos.modules.main

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import xyz.pavelkorolev.githubrepos.services.Navigator
import xyz.pavelkorolev.githubrepos.services.NavigatorImpl

@Subcomponent(modules = [
    MainModule::class
])
interface MainComponent {
    fun inject(mainActivity: MainActivity)
}

@Module
class MainModule(private val mainActivity: MainActivity) {

    @Provides
    fun providesContext(): Context = mainActivity

    @Provides
    fun provideNavigator(): Navigator = NavigatorImpl(mainActivity)

}
