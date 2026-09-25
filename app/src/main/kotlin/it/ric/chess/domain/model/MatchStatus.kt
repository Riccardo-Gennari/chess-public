package it.ric.chess.domain.model

import androidx.compose.runtime.Immutable

/**
 * Represents the status of a chess match.
 */
@Immutable
enum class MatchStatus {
    ONGOING,
    WHITE_WINS,
    BLACK_WINS,
    DRAW,
}
