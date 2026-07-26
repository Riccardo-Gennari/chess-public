package it.ric.chess.datasource.datastore

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

object Json {
    private val jsonSerializer =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    fun <T> decode(
        source: String,
        deserializer: DeserializationStrategy<T>,
    ): Result<T> {
        if (source.isBlank()) {
            return Result.failure(IllegalArgumentException("source is blank"))
        }

        return runCatching {
            jsonSerializer.decodeFromString(deserializer, source)
        }
    }

    fun <T> encode(
        source: T,
        serializer: SerializationStrategy<T>,
    ): Result<String> =
        runCatching {
            jsonSerializer.encodeToString(serializer, source)
        }

    inline fun <reified T> decode(source: String): Result<T> = decode(source, serializer())

    inline fun <reified T> encode(source: T): Result<String> = encode(source, serializer())
}
