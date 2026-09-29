package it.ric.chess.domain.usecase.auth

import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.mockk
import it.ric.chess.domain.repository.AuthRepository

class SignOutUseCaseTest :
    FunSpec({

        test("should call signOutFromPlayGames on authRepository") {
            val authRepo = mockk<AuthRepository>(relaxed = true)
            val useCase = SignOutUseCase(authRepo)

            useCase()

            coVerify { authRepo.signOutFromPlayGames() }
        }
    })
