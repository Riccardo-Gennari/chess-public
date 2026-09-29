package it.ric.chess.domain.handler

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import it.ric.chess.domain.repository.AuthRepository
import it.ric.chess.domain.repository.MatchRepository
import it.ric.chess.domain.repository.PlayerRepository

class MatchHandlerFactoryTest :
    FunSpec({

        test("should create LocalMatchHandler when matchId is null") {
            val matchRepo = mockk<MatchRepository>()
            val playerRepo = mockk<PlayerRepository>()
            val authRepo = mockk<AuthRepository>()

            val factory = DefaultMatchHandlerFactory(matchRepo, playerRepo, authRepo)
            val handler = factory.create(null)

            handler.shouldBeInstanceOf<LocalMatchHandler>()
        }

        test("should create RemoteMatchHandler when matchId is provided") {
            val matchRepo = mockk<MatchRepository>()
            val playerRepo = mockk<PlayerRepository>()
            val authRepo = mockk<AuthRepository> {
                every { uid } returns "user-1"
            }

            val factory = DefaultMatchHandlerFactory(matchRepo, playerRepo, authRepo)
            val handler = factory.create("match-1")

            handler.shouldBeInstanceOf<RemoteMatchHandler>()
        }
    })
