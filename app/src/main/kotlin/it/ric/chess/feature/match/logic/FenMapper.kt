package it.ric.chess.feature.match.logic

import it.ric.chess.feature.match.model.Board
import it.ric.chess.feature.match.model.Piece
import it.ric.chess.feature.match.model.PieceColor
import it.ric.chess.feature.match.model.PieceType
import it.ric.chess.feature.match.model.emptyBoard

/**
 * Converts a [Board] to its Forsyth-Edwards Notation (FEN) string representation.
 *
 * This implementation focuses on piece placement and the current turn.
 * Other FEN fields (castling, en passant, halfmove, fullmove) are currently simplified.
 *
 * @param turn The color of the player whose turn it is.
 * @return A FEN string representing the board state.
 */
fun Board.toFen(turn: PieceColor = PieceColor.WHITE): String = buildString {
    // 1. Piece placement
    for (row in 0..7) {
        var emptyCount = 0
        for (col in 0..7) {
            val piece = this@toFen[row][col]
            if (piece == null) {
                emptyCount++
            } else {
                if (emptyCount > 0) {
                    append(emptyCount)
                    emptyCount = 0
                }
                append(piece.toFenChar())
            }
        }
        if (emptyCount > 0) {
            append(emptyCount)
        }
        if (row < 7) append('/')
    }

    // 2. Active color
    append(" ")
    append(if (turn == PieceColor.WHITE) "w" else "b")

    // 3. Castling, 4. En passant, 5. Halfmove clock, 6. Fullmove number (simplified/placeholder)
    append(" - - 0 1")
}

/**
 * Parses a FEN string into a [Board] and the current [PieceColor] turn.
 *
 * @return A pair containing the [Board] and the active [PieceColor].
 * @throws IllegalArgumentException if the FEN string is malformed.
 */
fun String.toBoard(): Pair<Board, PieceColor> {
    val parts = split(" ")
    if (parts.isEmpty()) throw IllegalArgumentException("Empty FEN string")

    val rows = parts[0].split("/")
    if (rows.size != 8) throw IllegalArgumentException("FEN must have 8 rows, found ${rows.size}")

    val board = emptyBoard().map { it.toMutableList() }.toMutableList()

    for (rowIdx in 0..7) {
        val rowStr = rows[rowIdx]
        var colIdx = 0
        for (char in rowStr) {
            if (char.isDigit()) {
                val skip = char.digitToInt()
                colIdx += skip
            } else {
                if (colIdx >= 8) throw IllegalArgumentException("Row $rowIdx exceeds 8 columns")
                board[rowIdx][colIdx] = char.toPiece()
                colIdx++
            }
        }
        if (colIdx != 8) throw IllegalArgumentException("Row $rowIdx must have 8 columns, found $colIdx")
    }

    val turn = if (parts.getOrNull(1) == "b") PieceColor.BLACK else PieceColor.WHITE
    return Pair(board.map { it.toList() }, turn)
}

private fun Piece.toFenChar(): Char {
    val char = when (type) {
        PieceType.PAWN -> 'p'
        PieceType.KNIGHT -> 'n'
        PieceType.BISHOP -> 'b'
        PieceType.ROOK -> 'r'
        PieceType.QUEEN -> 'q'
        PieceType.KING -> 'k'
    }
    return if (color == PieceColor.WHITE) char.uppercaseChar() else char
}

private fun Char.toPiece(): Piece {
    val color = if (isUpperCase()) PieceColor.WHITE else PieceColor.BLACK
    val type = when (lowercaseChar()) {
        'p' -> PieceType.PAWN
        'n' -> PieceType.KNIGHT
        'b' -> PieceType.BISHOP
        'r' -> PieceType.ROOK
        'q' -> PieceType.QUEEN
        'k' -> PieceType.KING
        else -> throw IllegalArgumentException("Unknown piece type in FEN: $this")
    }
    return Piece(type, color)
}
