package it.ric.chess.data.datasource.datastore

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface UserPreferencesDataSource {
    val localMatchFen: Flow<String?>

    suspend fun saveLocalMatchFen(fen: String)

    suspend fun clearLocalMatchFen()
}

@Singleton
class DefaultUserPreferencesDataSource
    @Inject
    constructor(
        private val dataStore: DataStore,
    ) : UserPreferencesDataSource {
        private val localMatchKey = stringDataKey("local_match_fen")
        override val localMatchFen: Flow<String?> = dataStore[localMatchKey]

        override suspend fun saveLocalMatchFen(fen: String) {
            dataStore.put(localMatchKey, fen)
        }

        override suspend fun clearLocalMatchFen() {
            dataStore.remove(localMatchKey)
        }
    }
