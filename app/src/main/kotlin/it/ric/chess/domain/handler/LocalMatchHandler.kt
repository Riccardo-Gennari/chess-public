package it.ric.chess.domain.handler

import it.ric.chess.domain.logic.ChessRules
import it.ric.chess.domain.logic.toBoard
import it.ric.chess.domain.logic.toFen
import it.ric.chess.domain.model.Board
import it.ric.chess.domain.model.GameMode
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.model.initialBoard
import it.ric.chess.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalMatchHandler(
    private val matchRepository: MatchRepository,
) : MatchHandler {
    override val gameMode: GameMode = GameMode.LOCAL

    override fun observeState(): Flow<MatchStateUpdate> =
        matchRepository.observeLocalMatch().map { fen ->
            val (board, turn) = fen?.toBoard() ?: (initialBoard() to PieceColor.WHITE)
            val status = ChessRules.determineGameStatus(board, turn)
            MatchStateUpdate(
                board = board,
                turn = turn,
                status = status,
            )
        }

    override suspend fun onMove(
        nextBoard: Board,
        nextTurn: PieceColor,
        nextStatus: MatchStatus,
    ) {
        matchRepository.saveLocalMatch(nextBoard.toFen(nextTurn))
    }

    override suspend fun onReset() {
        matchRepository.clearLocalMatch()
    }

    override suspend fun onQuit() {
        matchRepository.clearLocalMatch()
    }
}
