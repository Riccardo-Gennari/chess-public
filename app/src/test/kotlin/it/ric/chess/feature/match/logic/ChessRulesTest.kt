package it.ric.chess.feature.match.logic

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import it.ric.chess.core.model.MatchStatus
import it.ric.chess.feature.match.model.Board
import it.ric.chess.feature.match.model.Piece
import it.ric.chess.feature.match.model.PieceColor
import it.ric.chess.feature.match.model.PieceType
import it.ric.chess.feature.match.model.emptyBoard
import it.ric.chess.feature.match.model.initialBoard

class ChessRulesTest : FunSpec({

    fun createBoardWithPieces(vararg pieces: Triple<Int, Int, Piece>): Board {
        val board = emptyBoard().map { it.toMutableList() }.toMutableList()
        for ((row, col, piece) in pieces) {
            board[row][col] = piece
        }
        return board.map { it.toList() }
    }

    context("Piece Movement") {
        test("Knight should move in L-shape and jump over pieces") {
            val board = createBoardWithPieces(
                Triple(4, 4, Piece(PieceType.KNIGHT, PieceColor.WHITE)),
                Triple(3, 4, Piece(PieceType.PAWN, PieceColor.WHITE)), // Piece directly in front
                Triple(2, 3, Piece(PieceType.PAWN, PieceColor.BLACK))  // Opponent piece to capture
            )
            val knight = board[4][4]!!
            val moves = ChessRules.calculateValidMoves(board, 4, 4, knight)

            moves shouldContain (2 to 3)
            moves shouldContain (2 to 5)
            moves shouldContain (3 to 2)
            moves shouldContain (3 to 6)
            moves shouldContain (5 to 2)
            moves shouldContain (5 to 6)
            moves shouldContain (6 to 3)
            moves shouldContain (6 to 5)
            moves.size shouldBe 8
        }

        test("Rook should be blocked by own pieces and capture opponent pieces") {
            val board = createBoardWithPieces(
                Triple(4, 4, Piece(PieceType.ROOK, PieceColor.WHITE)),
                Triple(4, 6, Piece(PieceType.PAWN, PieceColor.WHITE)), // Blocking friend
                Triple(2, 4, Piece(PieceType.PAWN, PieceColor.BLACK))  // Capture target
            )
            val rook = board[4][4]!!
            val moves = ChessRules.calculateValidMoves(board, 4, 4, rook)

            moves shouldContain (4 to 5)
            moves shouldNotContain (4 to 6)
            moves shouldNotContain (4 to 7)
            
            moves shouldContain (3 to 4)
            moves shouldContain (2 to 4)
            moves shouldNotContain (1 to 4)
            
            moves shouldContain (5 to 4)
            moves shouldContain (4 to 3)
        }

        test("Pawn should move forward and capture diagonally") {
            val board = createBoardWithPieces(
                Triple(6, 4, Piece(PieceType.PAWN, PieceColor.WHITE)),
                Triple(5, 4, Piece(PieceType.PAWN, PieceColor.BLACK)) // Blocking path
            )
            val pawn = board[6][4]!!
            var moves = ChessRules.calculateValidMoves(board, 6, 4, pawn)
            moves.size shouldBe 0 // Blocked

            val board2 = createBoardWithPieces(
                Triple(6, 4, Piece(PieceType.PAWN, PieceColor.WHITE)),
                Triple(5, 3, Piece(PieceType.PAWN, PieceColor.BLACK)) // Capture target
            )
            moves = ChessRules.calculateValidMoves(board2, 6, 4, board2[6][4]!!)
            moves shouldContain (5 to 4) // Step forward
            moves shouldContain (4 to 4) // Double step
            moves shouldContain (5 to 3) // Capture
        }
    }

    context("Check and Legal Moves") {
        test("King cannot move into check") {
            val board = createBoardWithPieces(
                Triple(0, 0, Piece(PieceType.KING, PieceColor.WHITE)),
                Triple(0, 7, Piece(PieceType.ROOK, PieceColor.BLACK))
            )
            val moves = ChessRules.calculateValidMoves(board, 0, 0, board[0][0]!!)
            moves shouldNotContain (0 to 1)
            moves shouldContain (1 to 0)
            moves shouldContain (1 to 1)
        }

        test("Pinned piece cannot move if it exposes King") {
            val board = createBoardWithPieces(
                Triple(7, 4, Piece(PieceType.KING, PieceColor.WHITE)),
                Triple(6, 4, Piece(PieceType.ROOK, PieceColor.WHITE)), // Pinned
                Triple(0, 4, Piece(PieceType.ROOK, PieceColor.BLACK))
            )
            val moves = ChessRules.calculateValidMoves(board, 6, 4, board[6][4]!!)
            moves shouldContain (5 to 4)
            moves shouldNotContain (6 to 3) // Exposes King
        }

        test("Double check MUST be escaped by King move") {
            val board = createBoardWithPieces(
                Triple(0, 0, Piece(PieceType.KING, PieceColor.BLACK)),
                Triple(0, 7, Piece(PieceType.ROOK, PieceColor.WHITE)),
                Triple(7, 0, Piece(PieceType.ROOK, PieceColor.WHITE)),
                Triple(5, 5, Piece(PieceType.PAWN, PieceColor.BLACK)) // Far away pawn
            )
            // Black is in double check from (0,7) and (7,0)
            ChessRules.isCheck(board, PieceColor.BLACK) shouldBe true
            
            // Pawn at (5,5) should have NO legal moves because it can't solve double check
            val pawnMoves = ChessRules.calculateValidMoves(board, 5, 5, board[5][5]!!)
            pawnMoves.size shouldBe 0
            
            // King at (0,0) must move to (1,1)
            val kingMoves = ChessRules.calculateValidMoves(board, 0, 0, board[0][0]!!)
            kingMoves shouldBe setOf(1 to 1)
        }
    }

    context("Game Status") {
        test("Simple checkmate detection") {
            val board = createBoardWithPieces(
                Triple(0, 0, Piece(PieceType.KING, PieceColor.BLACK)),
                Triple(1, 1, Piece(PieceType.QUEEN, PieceColor.WHITE)),
                Triple(2, 2, Piece(PieceType.KING, PieceColor.WHITE))
            )
            ChessRules.determineGameStatus(board, PieceColor.BLACK) shouldBe MatchStatus.WHITE_WINS
        }

        test("Stalemate detection") {
            val board = createBoardWithPieces(
                Triple(0, 0, Piece(PieceType.KING, PieceColor.BLACK)),
                Triple(2, 1, Piece(PieceType.KING, PieceColor.WHITE)),
                Triple(1, 2, Piece(PieceType.QUEEN, PieceColor.WHITE))
            )
            ChessRules.determineGameStatus(board, PieceColor.BLACK) shouldBe MatchStatus.DRAW
        }

        test("Fool's Mate") {
            var board = initialBoard()
            board = ChessRules.performMove(board, 6, 6, 4, 6, board[6][6]!!)
            board = ChessRules.performMove(board, 1, 4, 3, 4, board[1][4]!!)
            board = ChessRules.performMove(board, 6, 5, 5, 5, board[6][5]!!)
            board = ChessRules.performMove(board, 0, 3, 4, 7, board[0][3]!!)

            ChessRules.determineGameStatus(board, PieceColor.WHITE) shouldBe MatchStatus.BLACK_WINS
        }
        
        test("King capture is NOT a valid move") {
            val board = createBoardWithPieces(
                Triple(0, 0, Piece(PieceType.KING, PieceColor.BLACK)),
                Triple(1, 1, Piece(PieceType.QUEEN, PieceColor.WHITE))
            )
            val queenMoves = ChessRules.calculateValidMoves(board, 1, 1, board[1][1]!!)
            queenMoves shouldNotContain (0 to 0)
        }
    }
})
