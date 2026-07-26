package it.ric.chess.datasource.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Interface for reactive access to a single piece of persistent data.
 * It behaves like a [Flow] that emits the current value and provides methods to modify it.
 *
 * @param T The type of the data being persisted.
 */
interface PersistentData<T> : Flow<T?> {
    /** Sets a new [value] for this data. */
    suspend fun set(value: T): Result<Unit>

    /** Clears this piece of data from the store. */
    suspend fun clear(): Result<Unit>

    /** Atomically updates the data using the provided [transform] function. */
    suspend fun update(transform: (T?) -> T): Result<Unit>
}

/** Returns this [PersistentData] as a standard [Flow]. */
fun <T> PersistentData<T>.asFlow(): Flow<T?> = this

/** Returns a [Flow] that emits the value associated with the given [key] in a [PersistentData] map. */
operator fun <K, V> PersistentData<Map<K, V>>.get(key: K): Flow<V?> = this.map { it?.get(key) }

/** Updates the [PersistentData] map by setting the given [key] to the specified [value]. */
suspend fun <K, V> PersistentData<Map<K, V>>.set(
    key: K,
    value: V,
): Result<Unit> =
    this.update {
        val pair = key to value
        it?.plus(pair) ?: mapOf(pair)
    }
