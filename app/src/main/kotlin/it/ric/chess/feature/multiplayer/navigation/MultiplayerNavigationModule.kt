package it.ric.chess.feature.multiplayer.navigation

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import it.ric.chess.feature.multiplayer.multiplayerMenu
import it.ric.chess.navigation.nav3.EntryProviderInstaller

@Module
@InstallIn(ActivityRetainedComponent::class)
object MultiplayerNavigationModule {
    @Provides
    @IntoSet
    fun provideMultiplayerMenuEntry(): EntryProviderInstaller =
        {
            multiplayerMenu()
        }
}
