package it.ric.chess.domain.usecase.auth

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import it.ric.chess.domain.model.AuthUser
import it.ric.chess.domain.repository.AuthRepository
import kotlinx.coroutines.flow.flowOf

class GetAuthenticatedUserUseCaseTest :
    FunSpec({

        test("should delegate uid and authUser to authRepository") {
            val authUserFlow = flowOf(AuthUser("uid-1"))
            val authRepo = mockk<AuthRepository> {
                every { uid } returns "uid-1"
                every { authUser } returns authUserFlow
            }

            val useCase = GetAuthenticatedUserUseCase(authRepo)

            useCase.uid shouldBe "uid-1"
            useCase.authUser shouldBe authUserFlow
        }
    })
