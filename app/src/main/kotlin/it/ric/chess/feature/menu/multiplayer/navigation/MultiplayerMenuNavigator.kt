package it.ric.chess.feature.menu.multiplayer.navigation

import it.ric.chess.feature.match.navigation.Chessboard
import it.ric.chess.navigation.Navigator

interface MultiplayerMenuNavigator {
    fun navigateToMatch(matchId: String)

    fun navigateBack()
}

fun Navigator.multiplayerMenuNavigator() =
    object : MultiplayerMenuNavigator {
        override fun navigateToMatch(matchId: String) {
            push(Chessboard(matchId))
        }

        override fun navigateBack() {
            pop()
        }
    }
