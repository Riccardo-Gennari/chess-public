package it.ric.chess.datasource.firebase.dto

import com.google.firebase.database.PropertyName

/**
 * Data Transfer Object for a chess Match in Firebase Realtime Database.
 * Uses @PropertyName to keep keys minimal in the database while maintaining readability in code.
 */
data class MatchDto(
    @get:PropertyName(FIELD_ID) @set:PropertyName(FIELD_ID) var id: String = "",
    @get:PropertyName(FIELD_NAME) @set:PropertyName(FIELD_NAME) var name: String = "",
    @get:PropertyName(FIELD_WHITE) @set:PropertyName(FIELD_WHITE) var whitePlayerId: String? = null,
    @get:PropertyName(FIELD_BLACK) @set:PropertyName(FIELD_BLACK) var blackPlayerId: String? = null,
    @get:PropertyName(FIELD_WHITE_PGS) @set:PropertyName(FIELD_WHITE_PGS) var whitePgsId: String? = null,
    @get:PropertyName(FIELD_BLACK_PGS) @set:PropertyName(FIELD_BLACK_PGS) var blackPgsId: String? = null,
    @get:PropertyName(FIELD_FEN) @set:PropertyName(FIELD_FEN) var fen: String = "",
    @get:PropertyName(FIELD_STATUS) @set:PropertyName(FIELD_STATUS) var status: Int = 0,
    @get:PropertyName(FIELD_TIMESTAMP) @set:PropertyName(FIELD_TIMESTAMP) var timestamp: Long = 0
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_NAME = "n"
        const val FIELD_WHITE = "w"
        const val FIELD_BLACK = "b"
        const val FIELD_WHITE_PGS = "wp"
        const val FIELD_BLACK_PGS = "bp"
        const val FIELD_FEN = "f"
        const val FIELD_STATUS = "s"
        const val FIELD_TIMESTAMP = "t"
    }
}
