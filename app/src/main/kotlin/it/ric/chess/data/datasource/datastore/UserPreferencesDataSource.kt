package it.ric.chess.data.datasource.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface UserPreferencesDataSource {
    val isPlayGamesAuthAvailable: Flow<Boolean>

    suspend fun setPlayGamesAuthAvailable(available: Boolean)

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
        private val skipAuthKey = booleanDataKey("skipPlayGamesAuth")
        private val localMatchKey = stringDataKey("local_match_fen")

        private val _isPlayGamesAuthAvailable = dataStore.persistentData(skipAuthKey)

        override val isPlayGamesAuthAvailable: Flow<Boolean> =
            _isPlayGamesAuthAvailable.map { it ?: true }

        override suspend fun setPlayGamesAuthAvailable(available: Boolean) {
            _isPlayGamesAuthAvailable.set(!available)
        }

        override val localMatchFen: Flow<String?> = dataStore[localMatchKey]

        override suspend fun saveLocalMatchFen(fen: String) {
            dataStore.put(localMatchKey, fen)
        }

        override suspend fun clearLocalMatchFen() {
            dataStore.remove(localMatchKey)
        }
    }
