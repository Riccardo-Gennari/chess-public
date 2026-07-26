package it.ric.chess.feature.match.model

import it.ric.chess.navigation.Navigator

fun Navigator.chessboardNavigator() =
    object : ChessboardNavigator {
        override fun navigateBack() {
            pop()
        }
    }
