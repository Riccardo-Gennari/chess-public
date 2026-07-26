package it.ric.chess.datasource.datastore

import kotlinx.coroutines.flow.FlowCollector

@JvmInline
private value class DataStoreEntry<T>(
    private val data: Pair<DataStore, DataKey<*, T>>,
) : PersistentData<T> {
    private val dataStore get() = data.first
    private val key get() = data.second

    override suspend fun collect(collector: FlowCollector<T?>) = dataStore[key].collect(collector)

    override suspend fun set(value: T) = dataStore.put(key, value)

    override suspend fun clear() = dataStore.remove(key)

    override suspend fun update(transform: (T?) -> T) = dataStore.update(key, transform)
}

fun <T> DataStore.persistentData(key: DataKey<*, T>): PersistentData<T> = DataStoreEntry(this to key)
