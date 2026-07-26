package it.ric.chess.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    data class Chessboard(val matchId: String? = null) : Route

    class Menu : Route

    class MultiplayerMenu : Route
}
