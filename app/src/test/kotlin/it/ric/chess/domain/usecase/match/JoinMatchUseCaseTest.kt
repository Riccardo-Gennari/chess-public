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

class JoinMatchUseCaseTest :
    FunSpec({

        test("should return false if not authenticated") {
            val matchRepo = mockk<MatchRepository>()
            val authRepo = mockk<AuthRepository> {
                every { uid } returns null
            }
            val playerRepo = mockk<PlayerRepository>()

            val useCase = JoinMatchUseCase(matchRepo, authRepo, playerRepo)
            val result = useCase("match-1")

            result shouldBe false
        }

        test("should join match and return result when authenticated") {
            val matchRepo = mockk<MatchRepository> {
                coEvery { joinMatch("match-1", "user-1", "pgs-1") } returns true
            }
            val authRepo = mockk<AuthRepository> {
                every { uid } returns "user-1"
            }
            val playerRepo = mockk<PlayerRepository> {
                coEvery { getCurrentPlayerInfo() } returns PlayerInfo("pgs-1", "Player 1")
            }

            val useCase = JoinMatchUseCase(matchRepo, authRepo, playerRepo)
            val result = useCase("match-1")

            result shouldBe true
        }
    })
