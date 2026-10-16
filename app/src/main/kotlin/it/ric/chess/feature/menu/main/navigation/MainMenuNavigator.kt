package it.ric.chess.feature.menu.main.navigation

import it.ric.chess.feature.match.navigation.ChessboardRoute
import it.ric.chess.feature.menu.multiplayer.navigation.MultiplayerMenu
import it.ric.chess.navigation.Navigator

interface MainMenuNavigator {
    fun navigateToSinglePlayer()

    fun navigateToMultiplayerMenu()
}

fun Navigator.mainMenuNavigator() =
    object : MainMenuNavigator {
        override fun navigateToSinglePlayer() {
            push(ChessboardRoute())
        }

        override fun navigateToMultiplayerMenu() {
            push(MultiplayerMenu())
        }
    }
