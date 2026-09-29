package it.ric.chess.domain.usecase.match

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import it.ric.chess.domain.model.AuthUser
import it.ric.chess.domain.model.Match
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.repository.AuthRepository
import it.ric.chess.domain.repository.MatchRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class ObserveActiveMatchesUseCaseTest :
    FunSpec({

        test("should return empty list when user is null") {
            val matchRepo = mockk<MatchRepository>()
            val authRepo = mockk<AuthRepository> {
                every { authUser } returns flowOf(null)
            }

            val useCase = ObserveActiveMatchesUseCase(matchRepo, authRepo)
            val result = useCase().first()

            result shouldBe emptyList()
        }

        test("should return matches when user is authenticated") {
            val match = Match("m1", "Match 1", "u1", null, null, null, "fen", MatchStatus.ONGOING, 0L)
            val matchRepo = mockk<MatchRepository> {
                every { observeMyMatches("u1") } returns flowOf(listOf(match))
            }
            val authRepo = mockk<AuthRepository> {
                every { authUser } returns flowOf(AuthUser("u1"))
            }

            val useCase = ObserveActiveMatchesUseCase(matchRepo, authRepo)
            val result = useCase().first()

            result shouldBe listOf(match)
        }
    })
