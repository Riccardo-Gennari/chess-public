package it.ric.chess.feature.menu.navigation

import it.ric.chess.feature.match.navigation.ChessboardRoute
import it.ric.chess.feature.multiplayer.navigation.MultiplayerMenuRoute
import it.ric.chess.navigation.Navigator

interface MenuNavigator {
    fun navigateToSinglePlayer()

    fun navigateToMultiplayerMenu()
}

fun Navigator.menuNavigator() =
    object : MenuNavigator {
        override fun navigateToSinglePlayer() {
            push(ChessboardRoute())
        }

        override fun navigateToMultiplayerMenu() {
            push(MultiplayerMenuRoute())
        }
    }
