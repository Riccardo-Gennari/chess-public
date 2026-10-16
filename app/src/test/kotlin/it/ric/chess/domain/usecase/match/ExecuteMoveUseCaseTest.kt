package it.ric.chess.domain.usecase.match

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.model.initialBoard

class ExecuteMoveUseCaseTest :
    FunSpec({
        val useCase = ExecuteMoveUseCase(CalculateValidMovesUseCase())

        test("should execute valid move and return updated state") {
            val board = initialBoard()
            val result = useCase(
                board = board,
                fromRow = 6,
                fromCol = 0,
                toRow = 4,
                toCol = 0,
                currentTurn = PieceColor.WHITE,
            )

            result.shouldBeSuccess()
            val moveResult = result.getOrThrow()
            moveResult.nextTurn shouldBe PieceColor.BLACK
            moveResult.nextStatus shouldBe MatchStatus.ONGOING
            moveResult.nextBoard[4][0]?.hasMoved shouldBe true
            moveResult.nextBoard[6][0] shouldBe null
        }

        test("should return failure for invalid move target") {
            val board = initialBoard()
            val result = useCase(
                board = board,
                fromRow = 6,
                fromCol = 0,
                toRow = 3,
                toCol = 0, // Pawn cannot jump 3 squares
                currentTurn = PieceColor.WHITE,
            )

            result.shouldBeFailure()
        }

        test("should return failure for empty source square") {
            val board = initialBoard()
            val result = useCase(
                board = board,
                fromRow = 4,
                fromCol = 0, // Empty square
                toRow = 3,
                toCol = 0,
                currentTurn = PieceColor.WHITE,
            )

            result.shouldBeFailure()
        }

        test("should return failure when piece color does not match current turn") {
            val board = initialBoard()
            val result = useCase(
                board = board,
                fromRow = 1,
                fromCol = 0, // Black pawn
                toRow = 3,
                toCol = 0,
                currentTurn = PieceColor.WHITE, // Current turn is WHITE
            )

            result.shouldBeFailure()
        }
    })
