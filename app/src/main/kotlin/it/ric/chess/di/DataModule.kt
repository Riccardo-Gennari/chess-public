package it.ric.chess.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.ric.chess.core.log.Logger
import it.ric.chess.core.time.SystemTimeProvider
import it.ric.chess.core.time.TimeProvider
import it.ric.chess.data.datasource.datastore.DataStore
import it.ric.chess.data.datasource.datastore.DefaultUserPreferencesDataSource
import it.ric.chess.data.datasource.datastore.UserPreferencesDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindUserPreferencesDataSource(impl: DefaultUserPreferencesDataSource): UserPreferencesDataSource

    @Binds
    @Singleton
    abstract fun bindTimeProvider(impl: SystemTimeProvider): TimeProvider

    companion object {
        @Provides
        @Singleton
        fun provideDataStore(
            @ApplicationContext context: Context,
            logger: Logger,
        ): DataStore = DataStoreFactory.create(context, logger)
    }
}
