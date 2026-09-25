package it.ric.chess.domain.usecase.auth

import it.ric.chess.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() {
        authRepository.signOutFromPlayGames()
    }
}
