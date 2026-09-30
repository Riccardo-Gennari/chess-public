package it.ric.chess.di

import androidx.compose.runtime.mutableStateListOf
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import it.ric.chess.core.log.Logger
import it.ric.chess.feature.menu.navigation.MenuRoute
import it.ric.chess.navigation.Navigator
import it.ric.chess.navigation.nav3.Nav3Navigator

@Module
@InstallIn(ActivityRetainedComponent::class)
object NavigationCoreModule {
    @Provides
    @ActivityRetainedScoped
    fun provideNav3Navigator(logger: Logger): Nav3Navigator =
        Nav3Navigator(
            backStack = mutableStateListOf(MenuRoute()),
            log = logger,
        )

    @Provides
    fun provideNavigator(nav3Navigator: Nav3Navigator): Navigator = nav3Navigator
}
