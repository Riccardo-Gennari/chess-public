package it.ric.chess.feature.match.model

/**
 * Piece model for the chess game.
 * Board indexing: row 0 is the top, row 7 is the bottom; col 0 is the left, col 7 is the right.
 */

enum class PieceType {
    KING,
    QUEEN,
    ROOK,
    BISHOP,
    KNIGHT,
    PAWN,
}

enum class PieceColor {
    WHITE,
    BLACK,
}

/** Simple immutable piece data class. hasMoved tracks whether a piece has moved (useful for castling/en-passant). */
data class Piece(
    val type: PieceType,
    val color: PieceColor,
    val hasMoved: Boolean = false,
)

/** Board is an 8x8 immutable list of lists with nullable Piece entries. */
typealias Board = List<List<Piece?>>

/** Creates a copy of the board. */
fun Board.copy(): Board = map { it.toList() }

/** Create an empty 8x8 board. */
fun emptyBoard(): Board = List(8) { List(8) { null } }

/**
 * Create the standard initial chess setup.
 * Orientation: black pieces on rows 0..1 (top), white pieces on rows 6..7 (bottom).
 */
fun initialBoard(): Board {
    val board = emptyBoard().map { it.toMutableList() }.toMutableList()

    // Pawns
    for (col in 0 until 8) {
        board[1][col] = Piece(PieceType.PAWN, PieceColor.BLACK)
        board[6][col] = Piece(PieceType.PAWN, PieceColor.WHITE)
    }

    // Back-rank order
    val order =
        arrayOf(
            PieceType.ROOK,
            PieceType.KNIGHT,
            PieceType.BISHOP,
            PieceType.QUEEN,
            PieceType.KING,
            PieceType.BISHOP,
            PieceType.KNIGHT,
            PieceType.ROOK,
        )

    for (col in 0 until 8) {
        board[0][col] = Piece(order[col], PieceColor.BLACK)
        board[7][col] = Piece(order[col], PieceColor.WHITE)
    }

    return board.map { it.toList() }
}
