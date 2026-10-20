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
    constructor(
        private val calculateValidMovesUseCase: CalculateValidMovesUseCase,
    ) {
        operator fun invoke(
            board: Board,
            fromRow: Int,
            fromCol: Int,
            toRow: Int,
            toCol: Int,
            currentTurn: PieceColor,
            piece: Piece? = null,
        ): Result<MoveResult> {
            val targetPiece = piece ?: board[fromRow][fromCol]
                ?: return Result.failure(IllegalArgumentException("No piece at position ($fromRow, $fromCol)"))

            if (targetPiece.color != currentTurn) {
                return Result.failure(
                    IllegalStateException("Piece at ($fromRow, $fromCol) does not belong to active turn ($currentTurn)"),
                )
            }

            val validMoves = calculateValidMovesUseCase(board, fromRow, fromCol, targetPiece)
            if (!validMoves.contains(Pair(toRow, toCol))) {
                return Result.failure(
                    IllegalArgumentException("Move to ($toRow, $toCol) is invalid for piece $targetPiece"),
                )
            }

            val nextBoard = ChessRules.performMove(board, fromRow, fromCol, toRow, toCol, targetPiece)
            val nextTurn = ChessRules.getNextTurn(currentTurn)
            val nextStatus = ChessRules.determineGameStatus(nextBoard, nextTurn)
            return Result.success(MoveResult(nextBoard, nextTurn, nextStatus))
        }

        operator fun invoke(
            board: Board,
            fromRow: Int,
            fromCol: Int,
            toRow: Int,
            toCol: Int,
            piece: Piece,
            currentTurn: PieceColor,
        ): Result<MoveResult> =
            invoke(
                board = board,
                fromRow = fromRow,
                fromCol = fromCol,
                toRow = toRow,
                toCol = toCol,
                currentTurn = currentTurn,
                piece = piece,
            )
    }
