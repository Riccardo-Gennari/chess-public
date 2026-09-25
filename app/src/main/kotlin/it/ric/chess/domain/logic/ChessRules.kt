package it.ric.chess.domain.logic

import it.ric.chess.domain.model.Board
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.model.Piece
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.model.PieceType

/**
 * Core logic for chess rules, including movement validation, check detection,
 * and game status determination.
 */
object ChessRules {
    /**
     * Calculates all legal moves for a piece at the given position.
     * A legal move is a pseudo-legal move that does not leave the player's king in check.
     *
     * @param board The current state of the board.
     * @param row The current row of the piece.
     * @param col The current column of the piece.
     * @param piece The piece being moved.
     * @return A set of valid target coordinates (row, col).
     */
    fun calculateValidMoves(
        board: Board,
        row: Int,
        col: Int,
        piece: Piece,
    ): Set<Pair<Int, Int>> {
        val pseudoLegalMoves = calculatePseudoLegalMoves(board, row, col, piece)

        return pseudoLegalMoves
            .filter { (toRow, toCol) ->
                val nextBoard = performMove(board, row, col, toRow, toCol, piece)
                !isCheck(nextBoard, piece.color)
            }.toSet()
    }

    /**
     * Determines pseudo-legal moves based on piece type, ignoring check constraints.
     */
    private fun calculatePseudoLegalMoves(
        board: Board,
        row: Int,
        col: Int,
        piece: Piece,
    ): Set<Pair<Int, Int>> {
        val moves = mutableSetOf<Pair<Int, Int>>()
        when (piece.type) {
            PieceType.PAWN -> {
                calculatePawnMoves(board, row, col, piece, moves)
            }

            PieceType.KNIGHT -> {
                calculateKnightMoves(board, row, col, piece, moves)
            }

            PieceType.BISHOP -> {
                calculateBishopMoves(board, row, col, piece, moves)
            }

            PieceType.ROOK -> {
                calculateRookMoves(board, row, col, piece, moves)
            }

            PieceType.QUEEN -> {
                calculateBishopMoves(board, row, col, piece, moves)
                calculateRookMoves(board, row, col, piece, moves)
            }

            PieceType.KING -> {
                calculateKingMoves(board, row, col, piece, moves)
            }
        }
        return moves
    }

    private fun calculatePawnMoves(
        board: Board,
        row: Int,
        col: Int,
        piece: Piece,
        moves: MutableSet<Pair<Int, Int>>,
    ) {
        val direction = if (piece.color == PieceColor.WHITE) -1 else 1
        val startRow = if (piece.color == PieceColor.WHITE) 6 else 1

        // Single move forward
        val nextRow = row + direction
        if (nextRow in 0..7 && board[nextRow][col] == null) {
            moves.add(nextRow to col)
            // Double move forward from start row
            val doubleRow = row + 2 * direction
            if (row == startRow && board[doubleRow][col] == null) {
                moves.add(doubleRow to col)
            }
        }

        // Diagonal captures
        listOf(-1, 1).forEach { colDelta ->
            val targetCol = col + colDelta
            if (nextRow in 0..7 && targetCol in 0..7) {
                val targetPiece = board[nextRow][targetCol]
                if (targetPiece != null && targetPiece.color != piece.color && targetPiece.type != PieceType.KING) {
                    moves.add(nextRow to targetCol)
                }
            }
        }
    }

    private fun calculateKnightMoves(
        board: Board,
        row: Int,
        col: Int,
        piece: Piece,
        moves: MutableSet<Pair<Int, Int>>,
    ) {
        val offsets =
            listOf(
                -2 to -1,
                -2 to 1,
                -1 to -2,
                -1 to 2,
                1 to -2,
                1 to 2,
                2 to -1,
                2 to 1,
            )
        offsets.forEach { (dr, dc) ->
            val nr = row + dr
            val nc = col + dc
            if (nr in 0..7 && nc in 0..7) {
                val target = board[nr][nc]
                if (target == null || (target.color != piece.color && target.type != PieceType.KING)) {
                    moves.add(nr to nc)
                }
            }
        }
    }

    private fun calculateBishopMoves(
        board: Board,
        row: Int,
        col: Int,
        piece: Piece,
        moves: MutableSet<Pair<Int, Int>>,
    ) {
        addSlidingMoves(board, row, col, piece, moves, listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1))
    }

    private fun calculateRookMoves(
        board: Board,
        row: Int,
        col: Int,
        piece: Piece,
        moves: MutableSet<Pair<Int, Int>>,
    ) {
        addSlidingMoves(board, row, col, piece, moves, listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1))
    }

    private fun addSlidingMoves(
        board: Board,
        row: Int,
        col: Int,
        piece: Piece,
        moves: MutableSet<Pair<Int, Int>>,
        directions: List<Pair<Int, Int>>,
    ) {
        directions.forEach { (dr, dc) ->
            var nr = row + dr
            var nc = col + dc
            while (nr in 0..7 && nc in 0..7) {
                val target = board[nr][nc]
                if (target == null) {
                    moves.add(nr to nc)
                } else {
                    if (target.color != piece.color && target.type != PieceType.KING) {
                        moves.add(nr to nc)
                    }
                    break // Blocked
                }
                nr += dr
                nc += dc
            }
        }
    }

    private fun calculateKingMoves(
        board: Board,
        row: Int,
        col: Int,
        piece: Piece,
        moves: MutableSet<Pair<Int, Int>>,
    ) {
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = row + dr
                val nc = col + dc
                if (nr in 0..7 && nc in 0..7) {
                    val target = board[nr][nc]
                    if (target == null || (target.color != piece.color && target.type != PieceType.KING)) {
                        moves.add(nr to nc)
                    }
                }
            }
        }
    }

    /**
     * Executes a move on a virtual board and returns the new board state.
     */
    fun performMove(
        board: Board,
        fromRow: Int,
        fromCol: Int,
        toRow: Int,
        toCol: Int,
        piece: Piece,
    ): Board =
        board.mapIndexed { r, row ->
            row.mapIndexed { c, p ->
                when (r) {
                    toRow if c == toCol -> piece.copy(hasMoved = true)
                    fromRow if c == fromCol -> null
                    else -> p
                }
            }
        }

    fun getNextTurn(currentTurn: PieceColor): PieceColor = if (currentTurn == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE

    /**
     * Finds the King position for a given color.
     */
    fun findKing(
        board: Board,
        color: PieceColor,
    ): Pair<Int, Int>? {
        for (r in 0..7) {
            for (c in 0..7) {
                val p = board[r][c]
                if (p?.type == PieceType.KING && p.color == color) return r to c
            }
        }
        return null
    }

    /**
     * Optimized check detection.
     * Checks if the King of the specified [color] is under attack.
     */
    fun isCheck(
        board: Board,
        color: PieceColor,
    ): Boolean {
        val kingPos = findKing(board, color) ?: return false
        val (kr, kc) = kingPos
        val opponent = getNextTurn(color)

        // 1. Knight attacks
        val knightOffsets = listOf(-2 to -1, -2 to 1, -1 to -2, -1 to 2, 1 to -2, 1 to 2, 2 to -1, 2 to 1)
        for ((dr, dc) in knightOffsets) {
            val nr = kr + dr
            val nc = kc + dc
            if (nr in 0..7 && nc in 0..7) {
                val p = board[nr][nc]
                if (p?.type == PieceType.KNIGHT && p.color == opponent) return true
            }
        }

        // 2. Sliding piece attacks (Rook/Bishop/Queen) and King (adjacent)
        val slidingDirections =
            listOf(
                -1 to 0,
                1 to 0,
                0 to -1,
                0 to 1, // Straight
                -1 to -1,
                -1 to 1,
                1 to -1,
                1 to 1, // Diagonal
            )
        for ((dr, dc) in slidingDirections) {
            var nr = kr + dr
            var nc = kc + dc
            var distance = 1
            while (nr in 0..7 && nc in 0..7) {
                val p = board[nr][nc]
                if (p != null) {
                    if (p.color == opponent) {
                        val isStraight = dr == 0 || dc == 0
                        val type = p.type
                        if (type == PieceType.QUEEN) return true
                        if (isStraight && type == PieceType.ROOK) return true
                        if (!isStraight && type == PieceType.BISHOP) return true
                        if (distance == 1 && type == PieceType.KING) return true
                    }
                    break // Blocked by any piece
                }
                nr += dr
                nc += dc
                distance++
            }
        }

        // 3. Pawn attacks
        val opponentPawnRow = kr + (if (color == PieceColor.WHITE) -1 else 1)
        if (opponentPawnRow in 0..7) {
            listOf(-1, 1).forEach { dc ->
                val nc = kc + dc
                if (nc in 0..7) {
                    val p = board[opponentPawnRow][nc]
                    if (p?.type == PieceType.PAWN && p.color == opponent) return true
                }
            }
        }

        return false
    }

    /**
     * Checks if the player has any legal moves available.
     */
    fun hasLegalMoves(
        board: Board,
        color: PieceColor,
    ): Boolean {
        for (r in 0..7) {
            for (c in 0..7) {
                val p = board[r][c]
                if (p?.color == color) {
                    if (calculateValidMoves(board, r, c, p).isNotEmpty()) return true
                }
            }
        }
        return false
    }

    /**
     * Determines the game status (Ongoing, Checkmate, Stalemate).
     */
    fun determineGameStatus(
        board: Board,
        currentTurn: PieceColor,
    ): MatchStatus {
        findKing(board, currentTurn)
            ?: return if (currentTurn == PieceColor.WHITE) MatchStatus.BLACK_WINS else MatchStatus.WHITE_WINS

        val check = isCheck(board, currentTurn)
        val movesAvailable = hasLegalMoves(board, currentTurn)

        return when {
            !movesAvailable && check -> if (currentTurn == PieceColor.WHITE) MatchStatus.BLACK_WINS else MatchStatus.WHITE_WINS
            !movesAvailable && !check -> MatchStatus.DRAW
            else -> MatchStatus.ONGOING
        }
    }
}
