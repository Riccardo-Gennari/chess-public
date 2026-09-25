package it.ric.chess.domain.model

import androidx.compose.runtime.Immutable

@Immutable
enum class GameMode {
    LOCAL,
    REMOTE,
    BOT,
}
