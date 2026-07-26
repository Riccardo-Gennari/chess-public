package it.ric.chess.datasource.datastore

import androidx.datastore.preferences.core.Preferences

interface DataKey<K, V> {
    val prefKey: Preferences.Key<K>

    fun encode(value: V): Result<K>

    fun decode(keyValue: K): Result<V>
}
