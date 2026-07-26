package it.ric.chess.datasource.firebase.mapper

import it.ric.chess.core.model.Match
import it.ric.chess.core.model.MatchStatus
import it.ric.chess.datasource.firebase.dto.MatchDto

fun MatchDto.toDomain(): Match =
    Match(
        id = id,
        name = name,
        whitePlayerId = whitePlayerId,
        blackPlayerId = blackPlayerId,
        whitePgsId = whitePgsId,
        blackPgsId = blackPgsId,
        fen = fen,
        status = MatchStatus.entries.getOrElse(status) { MatchStatus.ONGOING },
        lastUpdate = timestamp,
    )

fun Match.toDto(): MatchDto =
    MatchDto(
        id = id,
        name = name,
        whitePlayerId = whitePlayerId,
        blackPlayerId = blackPlayerId,
        whitePgsId = whitePgsId,
        blackPgsId = blackPgsId,
        fen = fen,
        status = status.ordinal,
        timestamp = lastUpdate,
    )

fun createMoveUpdate(
    fen: String,
    status: MatchStatus,
): Map<String, Any?> =
    mapOf(
        MatchDto.FIELD_FEN to fen,
        MatchDto.FIELD_STATUS to status.ordinal,
        MatchDto.FIELD_TIMESTAMP to System.currentTimeMillis(),
    )

fun createJoinBlackUpdate(
    blackPlayerId: String,
    blackPgsId: String? = null,
): Map<String, Any?> =
    mapOf(
        MatchDto.FIELD_BLACK to blackPlayerId,
        MatchDto.FIELD_BLACK_PGS to blackPgsId,
        MatchDto.FIELD_TIMESTAMP to System.currentTimeMillis(),
    )
