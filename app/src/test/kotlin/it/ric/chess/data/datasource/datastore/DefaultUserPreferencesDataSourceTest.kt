package it.ric.chess.data.datasource.datastore

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf

class DefaultUserPreferencesDataSourceTest :
    FunSpec({

        test("localMatchFen should return value from dataStore") {
            val dataStore = mockk<DataStore>(relaxed = true) {
                every { get(any<DataKey<String, String>>()) } returns flowOf("fen-1")
            }
            val dataSource = DefaultUserPreferencesDataSource(dataStore)

            var result: String? = null
            dataSource.localMatchFen.collect { result = it }

            result shouldBe "fen-1"
        }

        test("saveLocalMatchFen should call dataStore put") {
            val dataStore = mockk<DataStore>(relaxed = true)
            val dataSource = DefaultUserPreferencesDataSource(dataStore)

            dataSource.saveLocalMatchFen("fen-1")

            coVerify { dataStore.put(any<DataKey<String, String>>(), "fen-1") }
        }

        test("clearLocalMatchFen should call dataStore remove") {
            val dataStore = mockk<DataStore>(relaxed = true)
            val dataSource = DefaultUserPreferencesDataSource(dataStore)

            dataSource.clearLocalMatchFen()

            coVerify { dataStore.remove(any<DataKey<String, String>>()) }
        }
    })
