package it.ric.chess.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import it.ric.chess.feature.match.navigation.ChessboardNavigator
import it.ric.chess.feature.match.navigation.chessboardNavigator
import it.ric.chess.feature.menu.navigation.MenuNavigator
import it.ric.chess.feature.menu.navigation.menuNavigator
import it.ric.chess.feature.multiplayer.navigation.MultiplayerMenuNavigator
import it.ric.chess.feature.multiplayer.navigation.multiplayerMenuNavigator
import it.ric.chess.navigation.Navigator

@Module
@InstallIn(ViewModelComponent::class)
object NavigationModule {
    @Provides
    fun provideMenuNavigator(navigator: Navigator): MenuNavigator = navigator.menuNavigator()

    @Provides
    fun provideMultiplayerMenuNavigator(navigator: Navigator): MultiplayerMenuNavigator =
        navigator.multiplayerMenuNavigator()

    @Provides
    fun provideChessboardNavigator(navigator: Navigator): ChessboardNavigator = navigator.chessboardNavigator()
}
