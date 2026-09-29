package it.ric.chess.data.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import it.ric.chess.core.time.TimeProvider
import it.ric.chess.data.datasource.datastore.UserPreferencesDataSource
import it.ric.chess.data.datasource.firebase.Firebase
import kotlinx.coroutines.flow.flowOf

class FirebaseMatchRepositoryTest :
    FunSpec({

        test("saveLocalMatch should call data source") {
            val firebase = mockk<Firebase>(relaxed = true)
            val ds = mockk<UserPreferencesDataSource>(relaxed = true)
            val time = mockk<TimeProvider>()

            val repo = FirebaseMatchRepository(firebase, ds, time)
            repo.saveLocalMatch("test-fen")

            coVerify { ds.saveLocalMatchFen("test-fen") }
        }

        test("clearLocalMatch should call data source") {
            val firebase = mockk<Firebase>(relaxed = true)
            val ds = mockk<UserPreferencesDataSource>(relaxed = true)
            val time = mockk<TimeProvider>()

            val repo = FirebaseMatchRepository(firebase, ds, time)
            repo.clearLocalMatch()

            coVerify { ds.clearLocalMatchFen() }
        }

        test("observeLocalMatch should return data source flow") {
            val firebase = mockk<Firebase>(relaxed = true)
            val ds = mockk<UserPreferencesDataSource> {
                every { localMatchFen } returns flowOf("test-fen")
            }
            val time = mockk<TimeProvider>()

            val repo = FirebaseMatchRepository(firebase, ds, time)
            var result: String? = null
            repo.observeLocalMatch().collect { result = it }

            result shouldBe "test-fen"
        }
    })
