package it.ric.chess.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.ric.chess.BuildConfig
import it.ric.chess.data.repository.DefaultPlayerRepository
import it.ric.chess.data.repository.FirebaseAuthRepository
import it.ric.chess.data.repository.FirebaseMatchRepository
import it.ric.chess.domain.handler.DefaultMatchHandlerFactory
import it.ric.chess.domain.handler.MatchHandlerFactory
import it.ric.chess.domain.repository.AuthRepository
import it.ric.chess.domain.repository.MatchRepository
import it.ric.chess.domain.repository.PlayerRepository
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPlayerRepository(impl: DefaultPlayerRepository): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMatchRepository(impl: FirebaseMatchRepository): MatchRepository

    @Binds
    @Singleton
    abstract fun bindMatchHandlerFactory(impl: DefaultMatchHandlerFactory): MatchHandlerFactory

    companion object {
        @Provides
        @Named("webClientId")
        fun provideWebClientId(): String = BuildConfig.WEB_CLIENT_ID
    }
}
