package it.ric.chess.datasource.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

class JsonDataKey<T>(
    override val prefKey: Preferences.Key<String>,
    private val serializer: KSerializer<T>,
) : DataKey<String, T> {
    override fun decode(keyValue: String): Result<T> = Json.decode(keyValue, serializer)

    override fun encode(value: T): Result<String> = Json.encode(value, serializer)
}

inline fun <reified T> jsonDataKey(name: String): JsonDataKey<T> =
    JsonDataKey(
        prefKey = stringPreferencesKey(name),
        serializer = serializer(),
    )
