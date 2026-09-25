package it.ric.chess.domain.usecase.match

import it.ric.chess.domain.logic.ChessRules
import it.ric.chess.domain.model.Board
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.model.Piece
import it.ric.chess.domain.model.PieceColor
import javax.inject.Inject

data class MoveResult(
    val nextBoard: Board,
    val nextTurn: PieceColor,
    val nextStatus: MatchStatus,
)

class ExecuteMoveUseCase
    @Inject
    constructor() {
        operator fun invoke(
            board: Board,
            fromRow: Int,
            fromCol: Int,
            toRow: Int,
            toCol: Int,
            piece: Piece,
            currentTurn: PieceColor,
        ): MoveResult {
            val nextBoard = ChessRules.performMove(board, fromRow, fromCol, toRow, toCol, piece)
            val nextTurn = ChessRules.getNextTurn(currentTurn)
            val nextStatus = ChessRules.determineGameStatus(nextBoard, nextTurn)
            return MoveResult(nextBoard, nextTurn, nextStatus)
        }
    }
