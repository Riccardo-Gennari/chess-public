package it.ric.chess.feature.match.logic

import it.ric.chess.core.model.GameMode
import it.ric.chess.core.model.MatchStatus
import it.ric.chess.feature.match.model.Board
import it.ric.chess.feature.match.model.PieceColor
import kotlinx.coroutines.flow.Flow

data class MatchStateUpdate(
    val board: Board,
    val turn: PieceColor,
    val status: MatchStatus,
    val playerColor: PieceColor? = null,
    val whitePlayerName: String? = null,
    val blackPlayerName: String? = null,
)

interface MatchHandler {
    val gameMode: GameMode
    fun observeState(): Flow<MatchStateUpdate>
    suspend fun onMove(nextBoard: Board, nextTurn: PieceColor, nextStatus: MatchStatus)
    suspend fun onReset()
    suspend fun onQuit()
}
