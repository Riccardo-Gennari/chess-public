package it.ric.chess.feature.match.logic

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.ric.chess.domain.logic.toBoard
import it.ric.chess.domain.logic.toFen
import it.ric.chess.domain.model.Board
import it.ric.chess.domain.model.Piece
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.model.PieceType
import it.ric.chess.domain.model.emptyBoard
import it.ric.chess.domain.model.initialBoard

class FenMapperTest :
    FunSpec({

        fun createBoardWithPieces(vararg pieces: Triple<Int, Int, Piece>): Board {
            val board = emptyBoard().map { it.toMutableList() }.toMutableList()
            for ((row, col, piece) in pieces) {
                board[row][col] = piece
            }
            return board.map { it.toList() }
        }

        context("toFen") {
            test("should correctly map initial board to FEN") {
                val board = initialBoard()
                // Standard initial position FEN (first part)
                val expected = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1"
                board.toFen(PieceColor.WHITE) shouldBe expected
            }

            test("should correctly map empty board to FEN") {
                val board = emptyBoard()
                val expected = "8/8/8/8/8/8/8/8 w - - 0 1"
                board.toFen(PieceColor.WHITE) shouldBe expected
            }

            test("should correctly map board with custom pieces and turn") {
                val board = createBoardWithPieces(
                    Triple(0, 0, Piece(PieceType.KING, PieceColor.BLACK)),
                    Triple(7, 7, Piece(PieceType.KING, PieceColor.WHITE)),
                    Triple(4, 4, Piece(PieceType.QUEEN, PieceColor.WHITE)),
                )
                val expected = "k7/8/8/8/4Q3/8/8/7K b - - 0 1"
                board.toFen(PieceColor.BLACK) shouldBe expected
            }
        }

        context("toBoard") {
            test("should correctly map initial FEN back to board and turn") {
                val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1"
                val (board, turn) = fen.toBoard()

                board shouldBe initialBoard()
                turn shouldBe PieceColor.WHITE
            }

            test("should correctly map custom FEN back to board and turn") {
                val fen = "k7/8/8/8/4Q3/8/8/7K b - - 0 1"
                val (board, turn) = fen.toBoard()

                board[0][0] shouldBe Piece(PieceType.KING, PieceColor.BLACK)
                board[4][4] shouldBe Piece(PieceType.QUEEN, PieceColor.WHITE)
                board[7][7] shouldBe Piece(PieceType.KING, PieceColor.WHITE)
                turn shouldBe PieceColor.BLACK
            }

            test("should throw exception for empty FEN") {
                shouldThrow<IllegalArgumentException> {
                    "".toBoard()
                }
            }

            test("should throw exception for FEN with missing rows") {
                shouldThrow<IllegalArgumentException> {
                    "8/8/8/8/8/8/8 w - - 0 1".toBoard()
                }
            }

            test("should throw exception for FEN with incorrect column count in a row") {
                shouldThrow<IllegalArgumentException> {
                    "9/8/8/8/8/8/8/8 w - - 0 1".toBoard()
                }
            }

            test("should throw exception for FEN with unknown piece char") {
                shouldThrow<IllegalArgumentException> {
                    "8/8/8/8/4X3/8/8/8 w - - 0 1".toBoard()
                }
            }
        }

        test("round trip: board -> fen -> board should remain consistent") {
            val originalBoard = initialBoard()
            val originalTurn = PieceColor.WHITE

            val fen = originalBoard.toFen(originalTurn)
            val (mappedBoard, mappedTurn) = fen.toBoard()

            mappedBoard shouldBe originalBoard
            mappedTurn shouldBe originalTurn
        }
    })
