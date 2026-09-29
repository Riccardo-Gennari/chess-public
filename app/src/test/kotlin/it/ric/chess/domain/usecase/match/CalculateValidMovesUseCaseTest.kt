package it.ric.chess.domain.usecase.match

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.ric.chess.domain.model.initialBoard

class CalculateValidMovesUseCaseTest :
    FunSpec({
        val useCase = CalculateValidMovesUseCase()

        test("should calculate valid moves for initial board pawn") {
            val board = initialBoard()
            val pawn = board[6][0]!!
            val moves = useCase(board, 6, 0, pawn)

            moves shouldBe setOf(5 to 0, 4 to 0)
        }
    })
