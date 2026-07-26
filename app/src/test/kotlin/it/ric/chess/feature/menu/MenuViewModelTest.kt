package it.ric.chess.feature.menu

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.ric.chess.core.log.Logger
import it.ric.chess.core.model.AuthUser
import it.ric.chess.core.model.PlayerInfo
import it.ric.chess.feature.match.advanceUntilIdle
import it.ric.chess.repository.AuthRepository
import it.ric.chess.repository.PlayerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest :
    FunSpec({

        fun setupMocks(): Triple<Logger, MenuNavigator, Pair<AuthRepository, PlayerRepository>> {
            val authUserFlow = MutableStateFlow<AuthUser?>(null)
            val mockAuthRepository =
                mockk<AuthRepository> {
                    every { authUser } returns authUserFlow
                    coEvery { signInWithPlayGames() } returns Unit
                    coEvery { signOutFromPlayGames() } returns Unit
                }
            val mockPlayerRepository =
                mockk<PlayerRepository> {
                    coEvery { getCurrentPlayerInfo() } returns PlayerInfo("id1", "Player 1")
                }
            val mockLogger = mockk<Logger>(relaxed = true)
            val navigator = mockk<MenuNavigator>(relaxed = true)
            return Triple(mockLogger, navigator, mockAuthRepository to mockPlayerRepository)
        }

        fun setupViewModel(log: Logger, authRepo: AuthRepository, playerRepo: PlayerRepository, nav: MenuNavigator) =
            MenuViewModel(
                log = log,
                authRepository = authRepo,
                playerRepository = playerRepo,
                navigator = nav,
                sharingStarted = SharingStarted.Eagerly,
            )

        test("initial state should be signed out and anonymous") {
            val (log, nav, repos) = setupMocks()
            val (authRepo, playerRepo) = repos
            val viewModel = setupViewModel(log, authRepo, playerRepo, nav)

            advanceUntilIdle()

            val state = viewModel.uiState.value
            state.isSignedIn shouldBe false
            state.playerInfo shouldBe PlayerInfo.anonymous
        }

        test("uiState should update when user signs in") {
            val (log, nav, repos) = setupMocks()
            val (authRepo, playerRepo) = repos
            val authUserFlow = authRepo.authUser as MutableStateFlow
            val viewModel = setupViewModel(log, authRepo, playerRepo, nav)

            advanceUntilIdle()
            viewModel.uiState.value.isSignedIn shouldBe false

            val testUser = AuthUser(uid = "uid1")
            val testPlayer = PlayerInfo("uid1", "Test User")
            coEvery { playerRepo.getCurrentPlayerInfo() } returns testPlayer

            authUserFlow.value = testUser
            advanceUntilIdle()

            val state = viewModel.uiState.value
            state.isSignedIn shouldBe true
            state.playerInfo shouldBe testPlayer
        }

        test("onSelectSinglePlayer should navigate to single player") {
            val (log, nav, repos) = setupMocks()
            val (authRepo, playerRepo) = repos
            val viewModel = setupViewModel(log, authRepo, playerRepo, nav)

            viewModel.onSelectSinglePlayer()
            verify { nav.navigateToSinglePlayer() }
        }

        test("onSelectMultiPlayer should navigate to multiplayer menu") {
            val (log, nav, repos) = setupMocks()
            val (authRepo, playerRepo) = repos
            val viewModel = setupViewModel(log, authRepo, playerRepo, nav)

            viewModel.onSelectMultiPlayer()
            verify { nav.navigateToMultiplayerMenu() }
        }

        test("onSignIn should call authRepository.signInWithPlayGames") {
            val (log, nav, repos) = setupMocks()
            val (authRepo, playerRepo) = repos
            val viewModel = setupViewModel(log, authRepo, playerRepo, nav)

            viewModel.onSignIn()
            advanceUntilIdle()

            coVerify { authRepo.signInWithPlayGames() }
        }

        test("onSignOut should call authRepository.signOutFromPlayGames") {
            val (log, nav, repos) = setupMocks()
            val (authRepo, playerRepo) = repos
            val viewModel = setupViewModel(log, authRepo, playerRepo, nav)

            viewModel.onSignOut()
            advanceUntilIdle()

            coVerify { authRepo.signOutFromPlayGames() }
        }
    })
