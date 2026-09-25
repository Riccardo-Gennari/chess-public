package it.ric.chess.domain.usecase.auth

import it.ric.chess.domain.model.PlayerInfo
import it.ric.chess.domain.repository.AuthRepository
import it.ric.chess.domain.repository.PlayerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetAuthenticatedPlayerUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val playerRepository: PlayerRepository,
    ) {
        operator fun invoke(): Flow<PlayerInfo> =
            authRepository.authUser
                .distinctUntilChanged()
                .mapLatest { user ->
                    if (user != null) {
                        playerRepository.getCurrentPlayerInfo() ?: PlayerInfo.anonymous
                    } else {
                        PlayerInfo.anonymous
                    }
                }
    }
