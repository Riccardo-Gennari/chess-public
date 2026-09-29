package it.ric.chess.domain.usecase.auth

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import it.ric.chess.domain.model.AuthUser
import it.ric.chess.domain.model.PlayerInfo
import it.ric.chess.domain.repository.AuthRepository
import it.ric.chess.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class GetAuthenticatedPlayerUseCaseTest :
    FunSpec({

        test("should return anonymous when user is null") {
            val authRepo = mockk<AuthRepository> {
                every { authUser } returns flowOf(null)
            }
            val playerRepo = mockk<PlayerRepository>()

            val useCase = GetAuthenticatedPlayerUseCase(authRepo, playerRepo)
            val result = useCase().first()

            result shouldBe PlayerInfo.anonymous
        }

        test("should return player info when user is authenticated") {
            val authRepo = mockk<AuthRepository> {
                every { authUser } returns flowOf(AuthUser("uid-1"))
            }
            val playerInfo = PlayerInfo("pgs-1", "Test Player")
            val playerRepo = mockk<PlayerRepository> {
                coEvery { getCurrentPlayerInfo() } returns playerInfo
            }

            val useCase = GetAuthenticatedPlayerUseCase(authRepo, playerRepo)
            val result = useCase().first()

            result shouldBe playerInfo
        }
    })
