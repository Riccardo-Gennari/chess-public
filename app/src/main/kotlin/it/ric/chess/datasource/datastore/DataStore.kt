package it.ric.chess.datasource.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import it.ric.chess.core.log.Logger
import it.ric.chess.core.log.tag
import it.ric.chess.core.log.withFixedTag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import androidx.datastore.core.DataStore as AndroidXDataStore

/**
 * A wrapper around AndroidX [DataStore] that provides type-safe access to persistent preferences.
 * It uses [it.ric.chess.datasource.datastore.DataKey]s to handle encoding and decoding of different data types (e.g., JSON, Strings).
 *
 * @param log The [Logger] used for tracking errors during encoding/decoding or I/O.
 * @param internalDataStore The underlying AndroidX [Preferences] [DataStore] implementation.
 */
class DataStore(
    log: Logger,
    private val internalDataStore: AndroidXDataStore<Preferences>,
) {
    private val log = log.withFixedTag(this.tag)

    /**
     * Observes the value associated with the given [dataKey].
     * If decoding fails, the value is removed from the store and null is emitted.
     *
     * @param dataKey The key identifying the data to retrieve.
     * @return A [Flow] emitting the current value, or null if it's not present or decoding fails.
     */
    operator fun <K, V> get(dataKey: DataKey<K, V>): Flow<V?> =
        internalDataStore.data
            .map { preferences -> preferences[dataKey.prefKey] }
            .distinctUntilChanged()
            .map { encodedValue ->
                encodedValue?.let {
                    val result = dataKey.decode(it)
                    if (result.isFailure) {
                        log.error("Decoding failure for '${dataKey.prefKey.name}'. Removing!", result.exceptionOrNull())
                        remove(dataKey)
                    }
                    result.getOrNull()
                }
            }.catch { exception ->
                log.error("Failure getting '${dataKey.prefKey.name}'", exception)
                emit(null)
            }

    /**
     * Persists the given [value] associated with the [dataKey].
     *
     * @return A [Result] indicating success or failure of the write operation.
     */
    suspend fun <K, V> put(
        dataKey: DataKey<K, V>,
        value: V,
    ): Result<Unit> =
        dataKey
            .encode(value)
            .onFailure { log.error("Encoding failure for '${dataKey.prefKey.name}'", it) }
            .mapCatching { encodedValue ->
                internalDataStore.edit { prefs ->
                    prefs[dataKey.prefKey] = encodedValue
                }
                Unit
            }.onFailure { log.error("Failure putting '${dataKey.prefKey.name}'", it) }

    /**
     * Removes the data associated with the given [dataKey] from the store.
     *
     * @return A [Result] indicating success or failure of the operation.
     */
    suspend fun <K, V> remove(dataKey: DataKey<K, V>): Result<Unit> =
        runCatching {
            internalDataStore.edit { it.remove(dataKey.prefKey) }
            Unit
        }.onFailure { log.error("Failure removing '${dataKey.prefKey.name}'", it) }

    /**
     * Atomically updates the value associated with [dataKey] using the provided [transform] function.
     *
     * @return A [Result] indicating success or failure of the operation.
     */
    suspend fun <K, V> update(
        dataKey: DataKey<K, V>,
        transform: (V?) -> V,
    ): Result<Unit> {
        return runCatching {
            internalDataStore.edit { preferences ->
                val initialValue = preferences[dataKey.prefKey]
                val decodedValue =
                    initialValue
                        ?.let { dataKey.decode(it) }
                        ?.onFailure { log.error("Decoding failure for '${dataKey.prefKey.name}'", it) }
                        ?.getOrNull()

                val transformedValue = transform(decodedValue)
                if (transformedValue == decodedValue) return@edit

                dataKey
                    .encode(transformedValue)
                    .onSuccess {
                        preferences[dataKey.prefKey] = it
                    }.onFailure {
                        log.error("Encoding failure for '${dataKey.prefKey.name}'", it)
                    }
            }
            Unit
        }.onFailure { log.error("Failure updating '${dataKey.prefKey.name}'", it) }
    }

    /**
     * Clears all data from this [DataStore].
     *
     * @return A [Result] indicating success or failure.
     */
    suspend fun clear(): Result<Unit> =
        runCatching {
            internalDataStore.edit { prefs ->
                prefs.clear()
            }
            Unit
        }.onFailure { throwable ->
            log.error("Failure clearing keys", throwable)
        }
}
