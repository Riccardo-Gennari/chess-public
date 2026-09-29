package it.ric.chess.domain.usecase.auth

import it.ric.chess.domain.model.AuthUser
import it.ric.chess.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthenticatedUserUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        val uid: String? get() = authRepository.uid
        val authUser: Flow<AuthUser?> get() = authRepository.authUser
    }
