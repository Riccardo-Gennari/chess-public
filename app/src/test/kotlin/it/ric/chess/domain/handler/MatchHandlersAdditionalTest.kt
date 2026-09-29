package it.ric.chess.domain.handler

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.repository.MatchRepository
import it.ric.chess.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class MatchHandlersAdditionalTest :
    FunSpec({

        test("RemoteMatchHandler should emit default initial state when match is null") {
            val matchRepo = mockk<MatchRepository> {
                every { observeMatch("m1") } returns flowOf(null)
            }
            val playerRepo = mockk<PlayerRepository>(relaxed = true)

            val handler = RemoteMatchHandler(matchRepo, playerRepo, "m1", "u1")
            val state = handler.observeState().first()

            state.status shouldBe MatchStatus.ONGOING
            state.turn shouldBe PieceColor.WHITE
        }

        test("LocalMatchHandler should observe initial fen when repo returns null") {
            val matchRepo = mockk<MatchRepository> {
                every { observeLocalMatch() } returns flowOf(null)
            }
            val handler = LocalMatchHandler(matchRepo)
            val state = handler.observeState().first()

            state.status shouldBe MatchStatus.ONGOING
            state.turn shouldBe PieceColor.WHITE
        }
    })
