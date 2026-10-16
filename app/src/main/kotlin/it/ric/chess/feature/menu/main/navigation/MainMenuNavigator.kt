package it.ric.chess.feature.menu.main.navigation

import it.ric.chess.feature.match.navigation.Chessboard
import it.ric.chess.feature.menu.multiplayer.navigation.MultiplayerMenu
import it.ric.chess.navigation.Navigator

interface MainMenuNavigator {
    fun navigateToSinglePlayer()

    fun navigateToMultiplayerMenu()
}

fun Navigator.mainMenuNavigator() =
    object : MainMenuNavigator {
        override fun navigateToSinglePlayer() {
            push(Chessboard())
        }

        override fun navigateToMultiplayerMenu() {
            push(MultiplayerMenu())
        }
    }
