package it.ric.chess.feature.menu.main.navigation

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import it.ric.chess.feature.menu.main.mainMenu
import it.ric.chess.navigation.nav3.EntryProviderInstaller

@Module
@InstallIn(ActivityRetainedComponent::class)
object MainMenuNavigationModule {
    @Provides
    @IntoSet
    fun provideMenuEntry(): EntryProviderInstaller =
        {
            mainMenu()
        }
}
