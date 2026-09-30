package it.ric.chess.feature.match.navigation

import it.ric.chess.navigation.Navigator

fun Navigator.chessboardNavigator() =
    object : ChessboardNavigator {
        override fun navigateBack() {
            pop()
        }
    }
