package it.ric.chess.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.ric.chess.core.log.Logger
import it.ric.chess.datasource.datastore.DataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
        logger: Logger,
    ): DataStore = DataStoreFactory.create(context, logger)
}
