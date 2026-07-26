package it.ric.chess.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.ric.chess.BuildConfig
import it.ric.chess.datasource.firebase.FirebaseAuthRepository
import it.ric.chess.datasource.firebase.FirebaseMatchRepository
import it.ric.chess.datasource.playgames.DefaultPlayerRepository
import it.ric.chess.feature.match.logic.DefaultMatchHandlerFactory
import it.ric.chess.feature.match.logic.MatchHandlerFactory
import it.ric.chess.repository.AuthRepository
import it.ric.chess.repository.MatchRepository
import it.ric.chess.repository.PlayerRepository
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
