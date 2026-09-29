package it.ric.chess.data.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import it.ric.chess.data.datasource.playgames.PlayGamesDataSource
import it.ric.chess.domain.model.PlayerInfo

class DefaultPlayerRepositoryTest :
    FunSpec({

        test("getCurrentPlayerInfo should delegate to datasource") {
            val dataSource = mockk<PlayGamesDataSource> {
                coEvery { getPlayerInfo() } returns PlayerInfo("p1", "Player 1")
            }
            val repo = DefaultPlayerRepository(dataSource)

            val info = repo.getCurrentPlayerInfo()
            info shouldBe PlayerInfo("p1", "Player 1")
        }

        test("getPlayerInfo by id should delegate to datasource") {
            val dataSource = mockk<PlayGamesDataSource> {
                coEvery { getPlayerInfo("p2") } returns PlayerInfo("p2", "Player 2")
            }
            val repo = DefaultPlayerRepository(dataSource)

            val info = repo.getPlayerInfo("p2")
            info shouldBe PlayerInfo("p2", "Player 2")
        }
    })
