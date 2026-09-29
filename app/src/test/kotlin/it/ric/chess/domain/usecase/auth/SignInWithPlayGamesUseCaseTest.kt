package it.ric.chess.domain.usecase.auth

import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.mockk
import it.ric.chess.domain.repository.AuthRepository

class SignInWithPlayGamesUseCaseTest :
    FunSpec({

        test("should call signInWithPlayGames on authRepository") {
            val authRepo = mockk<AuthRepository>(relaxed = true)
            val useCase = SignInWithPlayGamesUseCase(authRepo)

            useCase()

            coVerify { authRepo.signInWithPlayGames() }
        }
    })
