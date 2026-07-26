package it.ric.chess.feature.menu

import it.ric.chess.navigation.Navigator
import it.ric.chess.navigation.Route

interface MenuNavigator {
    fun navigateToSinglePlayer()
    fun navigateToMultiplayerMenu()
}

fun Navigator.menuNavigator() =
    object : MenuNavigator {
        override fun navigateToSinglePlayer() {
            push(Route.Chessboard())
        }

        override fun navigateToMultiplayerMenu() {
            push(Route.MultiplayerMenu())
        }
    }
