package it.ric.chess.data.datasource.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import it.ric.chess.core.log.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreTest :
    FunSpec({

        val testDispatcher = UnconfinedTestDispatcher()

        fun createTestStore(tempFile: File): DataStore {
            val logger = mockk<Logger>(relaxed = true)
            val androidXStore = PreferenceDataStoreFactory.create(
                scope = CoroutineScope(testDispatcher + SupervisorJob()),
                produceFile = { tempFile },
            )
            return DataStore(logger, androidXStore)
        }

        test("put and get value should work correctly") {
            runTest {
                val tempFile = File.createTempFile("test_prefs", ".preferences_pb")
                tempFile.deleteOnExit()
                val dataStore = createTestStore(tempFile)
                val key = stringDataKey("test_key")

                dataStore.put(key, "hello")
                val value = dataStore[key].first()

                value shouldBe "hello"
            }
        }
    })
