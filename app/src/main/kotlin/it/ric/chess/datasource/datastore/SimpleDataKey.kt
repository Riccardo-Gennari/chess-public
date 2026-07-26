package it.ric.chess.datasource.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class SimpleDataKey<T>(
    override val prefKey: Preferences.Key<T>,
) : DataKey<T, T> {
    override fun decode(keyValue: T) = Result.success(keyValue)

    override fun encode(value: T) = Result.success(value)
}

fun <T> Preferences.Key<T>.dataKey() = SimpleDataKey<T>(this)

fun stringDataKey(name: String) = stringPreferencesKey(name).dataKey()

fun booleanDataKey(name: String) = booleanPreferencesKey(name).dataKey()
