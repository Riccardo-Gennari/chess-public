package it.ric.chess.domain.handler

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import it.ric.chess.domain.logic.toFen
import it.ric.chess.domain.model.GameMode
import it.ric.chess.domain.model.Match
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.model.PlayerInfo
import it.ric.chess.domain.model.initialBoard
import it.ric.chess.domain.repository.MatchRepository
import it.ric.chess.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class RemoteMatchHandlerTest :
    FunSpec({

        test("gameMode should be REMOTE") {
            val matchRepo = mockk<MatchRepository>()
            val playerRepo = mockk<PlayerRepository>()
            val handler = RemoteMatchHandler(matchRepo, playerRepo, "match-1", "user-1")

            handler.gameMode shouldBe GameMode.REMOTE
        }

        test("observeState should emit match state and player color correctly") {
            val match = Match(
                id = "match-1",
                name = "Match",
                whitePlayerId = "user-1",
                blackPlayerId = "user-2",
                whitePgsId = "wp1",
                blackPgsId = "bp1",
                fen = initialBoard().toFen(PieceColor.WHITE),
                status = MatchStatus.ONGOING,
                lastUpdate = 0L,
            )

            val matchRepo = mockk<MatchRepository> {
                every { observeMatch("match-1") } returns flowOf(match)
            }
            val playerRepo = mockk<PlayerRepository> {
                coEvery { getPlayerInfo("wp1") } returns PlayerInfo("wp1", "White Player")
                coEvery { getPlayerInfo("bp1") } returns PlayerInfo("bp1", "Black Player")
            }

            val handler = RemoteMatchHandler(matchRepo, playerRepo, "match-1", "user-1")
            val state = handler.observeState().first()

            state.turn shouldBe PieceColor.WHITE
            state.status shouldBe MatchStatus.ONGOING
            state.playerColor shouldBe PieceColor.WHITE
        }

        test("onMove should call updateMove on repository") {
            val matchRepo = mockk<MatchRepository>(relaxed = true)
            val playerRepo = mockk<PlayerRepository>()
            val handler = RemoteMatchHandler(matchRepo, playerRepo, "match-1", "user-1")

            handler.onMove(initialBoard(), PieceColor.BLACK, MatchStatus.ONGOING)

            coVerify { matchRepo.updateMove("match-1", any(), MatchStatus.ONGOING) }
        }

        test("onQuit should call quitMatch on repository if currentUid is present") {
            val matchRepo = mockk<MatchRepository>(relaxed = true)
            val playerRepo = mockk<PlayerRepository>()
            val handler = RemoteMatchHandler(matchRepo, playerRepo, "match-1", "user-1")

            handler.onQuit()

            coVerify { matchRepo.quitMatch("match-1", "user-1") }
        }
    })
