package it.ric.chess.feature.match.logic

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import it.ric.chess.domain.handler.LocalMatchHandler
import it.ric.chess.domain.logic.toFen
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.model.Piece
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.model.PieceType
import it.ric.chess.domain.model.emptyBoard
import it.ric.chess.domain.repository.MatchRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class LocalMatchHandlerTest :
    FunSpec({

        val matchRepository = mockk<MatchRepository>(relaxed = true)
        val handler = LocalMatchHandler(matchRepository)

        test("observeState should calculate status from FEN") {
            // Set up a board that is in checkmate
            // White wins: Black King (0,0), White Queen (1,1), White King (2,2)
            val board = emptyBoard().map { it.toMutableList() }.toMutableList()
            board[0][0] = Piece(PieceType.KING, PieceColor.BLACK)
            board[1][1] = Piece(PieceType.QUEEN, PieceColor.WHITE)
            board[2][2] = Piece(PieceType.KING, PieceColor.WHITE)
            val fen = board.toFen(PieceColor.BLACK)

            every { matchRepository.observeLocalMatch() } returns flowOf(fen)

            val state = handler.observeState().first()

            state.status shouldBe MatchStatus.WHITE_WINS
            state.turn shouldBe PieceColor.BLACK
        }

        test("onMove should always save to repository") {
            val board = emptyBoard()
            val nextTurn = PieceColor.BLACK
            val nextStatus = MatchStatus.WHITE_WINS

            handler.onMove(board, nextTurn, nextStatus)

            coVerify { matchRepository.saveLocalMatch(any()) }
        }
    })
