package it.ric.chess.domain.usecase.match

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import it.ric.chess.domain.model.Match
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.repository.MatchRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class ObserveWaitingMatchesUseCaseTest :
    FunSpec({

        test("should filter out active matches from waiting matches") {
            val match1 = Match("m1", "Match 1", "u1", null, null, null, "fen", MatchStatus.ONGOING, 0L)
            val match2 = Match("m2", "Match 2", "u2", null, null, null, "fen", MatchStatus.ONGOING, 0L)

            val matchRepo = mockk<MatchRepository> {
                every { observeWaitingMatches() } returns flowOf(listOf(match1, match2))
            }
            val activeUseCase = mockk<ObserveActiveMatchesUseCase> {
                every { this@mockk.invoke() } returns flowOf(listOf(match1))
            }

            val useCase = ObserveWaitingMatchesUseCase(matchRepo, activeUseCase)
            val result = useCase().first()

            result shouldBe listOf(match2)
        }
    })
