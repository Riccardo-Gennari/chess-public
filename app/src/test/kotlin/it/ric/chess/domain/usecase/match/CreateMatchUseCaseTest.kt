package it.ric.chess.domain.usecase.match

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import it.ric.chess.domain.model.PlayerInfo
import it.ric.chess.domain.repository.AuthRepository
import it.ric.chess.domain.repository.MatchRepository
import it.ric.chess.domain.repository.PlayerRepository

class CreateMatchUseCaseTest :
    FunSpec({

        test("should return null if not authenticated") {
            val matchRepo = mockk<MatchRepository>()
            val authRepo = mockk<AuthRepository> {
                every { uid } returns null
            }
            val playerRepo = mockk<PlayerRepository>()

            val useCase = CreateMatchUseCase(matchRepo, authRepo, playerRepo)
            val result = useCase("My Match")

            result shouldBe null
        }

        test("should create match and return match id when authenticated") {
            val matchRepo = mockk<MatchRepository> {
                coEvery { createMatch(any(), any(), any(), any()) } returns "match-123"
            }
            val authRepo = mockk<AuthRepository> {
                every { uid } returns "user-1"
            }
            val playerRepo = mockk<PlayerRepository> {
                coEvery { getCurrentPlayerInfo() } returns PlayerInfo("pgs-1", "Player 1")
            }

            val useCase = CreateMatchUseCase(matchRepo, authRepo, playerRepo)
            val result = useCase("My Match")

            result shouldBe "match-123"
        }
    })
