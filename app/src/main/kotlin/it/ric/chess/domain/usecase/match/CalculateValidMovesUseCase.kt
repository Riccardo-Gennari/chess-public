package it.ric.chess.domain.usecase.match

import it.ric.chess.domain.logic.ChessRules
import it.ric.chess.domain.model.Board
import it.ric.chess.domain.model.Piece
import javax.inject.Inject

class CalculateValidMovesUseCase
    @Inject
    constructor() {
        operator fun invoke(
            board: Board,
            row: Int,
            col: Int,
            piece: Piece,
        ): Set<Pair<Int, Int>> = ChessRules.calculateValidMoves(board, row, col, piece)
    }
