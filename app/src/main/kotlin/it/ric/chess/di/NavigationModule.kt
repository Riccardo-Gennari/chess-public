package it.ric.chess.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import it.ric.chess.feature.match.navigation.ChessboardNavigator
import it.ric.chess.feature.match.navigation.chessboardNavigator
import it.ric.chess.feature.menu.main.navigation.MainMenuNavigator
import it.ric.chess.feature.menu.main.navigation.mainMenuNavigator
import it.ric.chess.feature.menu.multiplayer.navigation.MultiplayerMenuNavigator
import it.ric.chess.feature.menu.multiplayer.navigation.multiplayerMenuNavigator
import it.ric.chess.navigation.Navigator

@Module
@InstallIn(ViewModelComponent::class)
object NavigationModule {
    @Provides
    fun provideMenuNavigator(navigator: Navigator): MainMenuNavigator = navigator.mainMenuNavigator()

    @Provides
    fun provideMultiplayerMenuNavigator(navigator: Navigator): MultiplayerMenuNavigator =
        navigator.multiplayerMenuNavigator()

    @Provides
    fun provideChessboardNavigator(navigator: Navigator): ChessboardNavigator = navigator.chessboardNavigator()
}
