package it.ric.chess.domain.usecase.match

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.model.initialBoard

class ExecuteMoveUseCaseTest :
    FunSpec({
        val useCase = ExecuteMoveUseCase()

        test("should execute valid move and return updated state") {
            val board = initialBoard()
            val pawn = board[6][0]!!
            val result = useCase(
                board = board,
                fromRow = 6,
                fromCol = 0,
                toRow = 4,
                toCol = 0,
                piece = pawn,
                currentTurn = PieceColor.WHITE,
            )

            result.nextTurn shouldBe PieceColor.BLACK
            result.nextStatus shouldBe MatchStatus.ONGOING
            result.nextBoard[4][0]?.hasMoved shouldBe true
            result.nextBoard[6][0] shouldBe null
        }
    })
