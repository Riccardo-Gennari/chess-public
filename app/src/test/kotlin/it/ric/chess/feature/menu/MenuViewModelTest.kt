package it.ric.chess.feature.menu

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.ric.chess.core.log.Logger
import it.ric.chess.domain.model.PlayerInfo
import it.ric.chess.domain.usecase.auth.GetAuthenticatedPlayerUseCase
import it.ric.chess.domain.usecase.auth.SignInWithPlayGamesUseCase
import it.ric.chess.domain.usecase.auth.SignOutUseCase
import it.ric.chess.feature.match.advanceUntilIdle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest :
    FunSpec({

        fun setupMocks(): Pair<Logger, MenuNavigator> {
            val mockLogger = mockk<Logger>(relaxed = true)
            val navigator = mockk<MenuNavigator>(relaxed = true)
            return mockLogger to navigator
        }

        test("initial state should be signed out and anonymous") {
            val (log, nav) = setupMocks()
            val playerFlow = MutableStateFlow(PlayerInfo.anonymous)
            val mockGetAuthPlayerUseCase = mockk<GetAuthenticatedPlayerUseCase> {
                every { this@mockk.invoke() } returns playerFlow
            }
            val mockSignInUseCase = mockk<SignInWithPlayGamesUseCase>(relaxed = true)
            val mockSignOutUseCase = mockk<SignOutUseCase>(relaxed = true)

            val viewModel = MenuViewModel(
                log = log,
                getAuthenticatedPlayerUseCase = mockGetAuthPlayerUseCase,
                signInWithPlayGamesUseCase = mockSignInUseCase,
                signOutUseCase = mockSignOutUseCase,
                navigator = nav,
                sharingStarted = SharingStarted.Eagerly,
            )

            advanceUntilIdle()

            val state = viewModel.uiState.value
            state.isSignedIn shouldBe false
            state.playerInfo shouldBe PlayerInfo.anonymous
        }

        test("uiState should update when user signs in") {
            val (log, nav) = setupMocks()
            val playerFlow = MutableStateFlow(PlayerInfo.anonymous)
            val mockGetAuthPlayerUseCase = mockk<GetAuthenticatedPlayerUseCase> {
                every { this@mockk.invoke() } returns playerFlow
            }
            val mockSignInUseCase = mockk<SignInWithPlayGamesUseCase>(relaxed = true)
            val mockSignOutUseCase = mockk<SignOutUseCase>(relaxed = true)

            val viewModel = MenuViewModel(
                log = log,
                getAuthenticatedPlayerUseCase = mockGetAuthPlayerUseCase,
                signInWithPlayGamesUseCase = mockSignInUseCase,
                signOutUseCase = mockSignOutUseCase,
                navigator = nav,
                sharingStarted = SharingStarted.Eagerly,
            )

            advanceUntilIdle()
            viewModel.uiState.value.isSignedIn shouldBe false

            val testPlayer = PlayerInfo("uid1", "Test User")
            playerFlow.value = testPlayer
            advanceUntilIdle()

            val state = viewModel.uiState.value
            state.isSignedIn shouldBe true
            state.playerInfo shouldBe testPlayer
        }

        test("onSelectSinglePlayer should navigate to single player") {
            val (log, nav) = setupMocks()
            val playerFlow = MutableStateFlow(PlayerInfo.anonymous)
            val mockGetAuthPlayerUseCase = mockk<GetAuthenticatedPlayerUseCase> {
                every { this@mockk.invoke() } returns playerFlow
            }
            val mockSignInUseCase = mockk<SignInWithPlayGamesUseCase>(relaxed = true)
            val mockSignOutUseCase = mockk<SignOutUseCase>(relaxed = true)

            val viewModel = MenuViewModel(
                log = log,
                getAuthenticatedPlayerUseCase = mockGetAuthPlayerUseCase,
                signInWithPlayGamesUseCase = mockSignInUseCase,
                signOutUseCase = mockSignOutUseCase,
                navigator = nav,
                sharingStarted = SharingStarted.Eagerly,
            )

            viewModel.onSelectSinglePlayer()
            verify { nav.navigateToSinglePlayer() }
        }

        test("onSelectMultiPlayer should navigate to multiplayer menu") {
            val (log, nav) = setupMocks()
            val playerFlow = MutableStateFlow(PlayerInfo.anonymous)
            val mockGetAuthPlayerUseCase = mockk<GetAuthenticatedPlayerUseCase> {
                every { this@mockk.invoke() } returns playerFlow
            }
            val mockSignInUseCase = mockk<SignInWithPlayGamesUseCase>(relaxed = true)
            val mockSignOutUseCase = mockk<SignOutUseCase>(relaxed = true)

            val viewModel = MenuViewModel(
                log = log,
                getAuthenticatedPlayerUseCase = mockGetAuthPlayerUseCase,
                signInWithPlayGamesUseCase = mockSignInUseCase,
                signOutUseCase = mockSignOutUseCase,
                navigator = nav,
                sharingStarted = SharingStarted.Eagerly,
            )

            viewModel.onSelectMultiPlayer()
            verify { nav.navigateToMultiplayerMenu() }
        }

        test("onSignIn should call signInWithPlayGamesUseCase") {
            val (log, nav) = setupMocks()
            val playerFlow = MutableStateFlow(PlayerInfo.anonymous)
            val mockGetAuthPlayerUseCase = mockk<GetAuthenticatedPlayerUseCase> {
                every { this@mockk.invoke() } returns playerFlow
            }
            val mockSignInUseCase = mockk<SignInWithPlayGamesUseCase>(relaxed = true)
            val mockSignOutUseCase = mockk<SignOutUseCase>(relaxed = true)

            val viewModel = MenuViewModel(
                log = log,
                getAuthenticatedPlayerUseCase = mockGetAuthPlayerUseCase,
                signInWithPlayGamesUseCase = mockSignInUseCase,
                signOutUseCase = mockSignOutUseCase,
                navigator = nav,
                sharingStarted = SharingStarted.Eagerly,
            )

            viewModel.onSignIn()
            advanceUntilIdle()

            coVerify { mockSignInUseCase() }
        }

        test("onSignOut should call signOutUseCase") {
            val (log, nav) = setupMocks()
            val playerFlow = MutableStateFlow(PlayerInfo.anonymous)
            val mockGetAuthPlayerUseCase = mockk<GetAuthenticatedPlayerUseCase> {
                every { this@mockk.invoke() } returns playerFlow
            }
            val mockSignInUseCase = mockk<SignInWithPlayGamesUseCase>(relaxed = true)
            val mockSignOutUseCase = mockk<SignOutUseCase>(relaxed = true)

            val viewModel = MenuViewModel(
                log = log,
                getAuthenticatedPlayerUseCase = mockGetAuthPlayerUseCase,
                signInWithPlayGamesUseCase = mockSignInUseCase,
                signOutUseCase = mockSignOutUseCase,
                navigator = nav,
                sharingStarted = SharingStarted.Eagerly,
            )

            viewModel.onSignOut()
            advanceUntilIdle()

            coVerify { mockSignOutUseCase() }
        }
    })
