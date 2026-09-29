package it.ric.chess.data.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.ric.chess.core.log.Logger
import it.ric.chess.data.datasource.datastore.UserPreferencesDataSource
import it.ric.chess.data.datasource.firebase.Firebase
import it.ric.chess.data.datasource.playgames.PlayGamesDataSource
import kotlinx.coroutines.flow.flowOf

class FirebaseAuthRepositoryTest :
    FunSpec({

        test("uid should return firebase uid") {
            val prefs = mockk<UserPreferencesDataSource> {
                every { isPlayGamesAuthAvailable } returns flowOf(true)
            }
            val firebase = mockk<Firebase> {
                every { uid } returns "firebase-uid-1"
                every { observeAuthState() } returns flowOf(null)
            }
            val playGames = mockk<PlayGamesDataSource>()
            val logger = mockk<Logger>(relaxed = true)

            val repo = FirebaseAuthRepository(prefs, firebase, playGames, logger, "client-id")

            repo.uid shouldBe "firebase-uid-1"
        }

        test("signOutFromPlayGames should sign out from play games and firebase") {
            val prefs = mockk<UserPreferencesDataSource>(relaxed = true) {
                every { isPlayGamesAuthAvailable } returns flowOf(true)
            }
            val firebase = mockk<Firebase>(relaxed = true) {
                every { observeAuthState() } returns flowOf(null)
            }
            val playGames = mockk<PlayGamesDataSource>(relaxed = true)
            val logger = mockk<Logger>(relaxed = true)

            val repo = FirebaseAuthRepository(prefs, firebase, playGames, logger, "client-id")
            repo.signOutFromPlayGames()

            coVerify { playGames.signOut() }
            coVerify { prefs.setPlayGamesAuthAvailable(false) }
            verify { firebase.signOut() }
        }
    })
