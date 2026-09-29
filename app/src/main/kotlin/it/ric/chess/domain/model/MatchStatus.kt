package it.ric.chess.domain.model

/**
 * Represents the status of a chess match.
 */
enum class MatchStatus {
    ONGOING,
    WHITE_WINS,
    BLACK_WINS,
    DRAW,
}
