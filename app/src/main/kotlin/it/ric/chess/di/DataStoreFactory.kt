package it.ric.chess.di

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import it.ric.chess.core.log.Logger
import it.ric.chess.core.log.tag
import it.ric.chess.datasource.datastore.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object DataStoreFactory {
    private const val USER_PREFERENCES_NAME = "user_preferences"

    fun create(
        context: Context,
        log: Logger,
    ): DataStore =
        DataStore(
            log,
            internalDataStore =
                PreferenceDataStoreFactory.create(
                    corruptionHandler =
                        ReplaceFileCorruptionHandler {
                            log.warning(tag, "Corrupted preferences found. Clearing...", it)
                            emptyPreferences()
                        },
                    scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                    produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES_NAME) },
                ),
        )
}
