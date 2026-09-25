package it.ric.chess.feature.multiplayer

import it.ric.chess.navigation.Navigator
import it.ric.chess.navigation.Route

interface MultiplayerMenuNavigator {
    fun navigateToMatch(matchId: String)

    fun navigateBack()
}

fun Navigator.multiplayerMenuNavigator() =
    object : MultiplayerMenuNavigator {
        override fun navigateToMatch(matchId: String) {
            push(Route.Chessboard(matchId))
        }

        override fun navigateBack() {
            pop()
        }
    }
