package it.ric.chess.feature.multiplayer.navigation

import it.ric.chess.feature.match.navigation.ChessboardRoute
import it.ric.chess.navigation.Navigator

interface MultiplayerMenuNavigator {
    fun navigateToMatch(matchId: String)

    fun navigateBack()
}

fun Navigator.multiplayerMenuNavigator() =
    object : MultiplayerMenuNavigator {
        override fun navigateToMatch(matchId: String) {
            push(ChessboardRoute(matchId))
        }

        override fun navigateBack() {
            pop()
        }
    }
