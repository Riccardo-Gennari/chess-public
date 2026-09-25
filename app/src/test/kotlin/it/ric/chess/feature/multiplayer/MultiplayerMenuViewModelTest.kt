package it.ric.chess.feature.multiplayer

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.ric.chess.core.log.Logger
import it.ric.chess.domain.model.Match
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.repository.AuthRepository
import it.ric.chess.domain.usecase.auth.SignInWithPlayGamesUseCase
import it.ric.chess.domain.usecase.match.CreateMatchUseCase
import it.ric.chess.domain.usecase.match.JoinMatchUseCase
import it.ric.chess.domain.usecase.match.ObserveActiveMatchesUseCase
import it.ric.chess.domain.usecase.match.ObserveWaitingMatchesUseCase
import it.ric.chess.feature.match.advanceUntilIdle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted

@OptIn(ExperimentalCoroutinesApi::class)
class MultiplayerMenuViewModelTest :
    FunSpec({

        data class Mocks(
            val log: Logger,
            val nav: MultiplayerMenuNavigator,
            val authRepo: AuthRepository,
            val signInUseCase: SignInWithPlayGamesUseCase,
            val observeActiveUseCase: ObserveActiveMatchesUseCase,
            val observeWaitingUseCase: ObserveWaitingMatchesUseCase,
            val createMatchUseCase: CreateMatchUseCase,
            val joinMatchUseCase: JoinMatchUseCase,
            val activeMatchesFlow: MutableStateFlow<List<Match>>,
            val waitingMatchesFlow: MutableStateFlow<List<Match>>,
        )

        fun setupMocks(): Mocks {
            val activeMatchesFlow = MutableStateFlow<List<Match>>(emptyList())
            val waitingMatchesFlow = MutableStateFlow<List<Match>>(emptyList())

            val authRepo = mockk<AuthRepository>(relaxed = true) {
                every { uid } returns "test-uid"
            }
            val signInUseCase = mockk<SignInWithPlayGamesUseCase>(relaxed = true)
            val observeActiveUseCase = mockk<ObserveActiveMatchesUseCase> {
                every { this@mockk.invoke() } returns activeMatchesFlow
            }
            val observeWaitingUseCase = mockk<ObserveWaitingMatchesUseCase> {
                every { this@mockk.invoke() } returns waitingMatchesFlow
            }
            val createMatchUseCase = mockk<CreateMatchUseCase>(relaxed = true)
            val joinMatchUseCase = mockk<JoinMatchUseCase>(relaxed = true)
            val log = mockk<Logger>(relaxed = true)
            val nav = mockk<MultiplayerMenuNavigator>(relaxed = true)

            return Mocks(
                log,
                nav,
                authRepo,
                signInUseCase,
                observeActiveUseCase,
                observeWaitingUseCase,
                createMatchUseCase,
                joinMatchUseCase,
                activeMatchesFlow,
                waitingMatchesFlow,
            )
        }

        fun setupViewModel(mocks: Mocks) =
            MultiplayerMenuViewModel(
                log = mocks.log,
                authRepository = mocks.authRepo,
                signInWithPlayGamesUseCase = mocks.signInUseCase,
                observeActiveMatchesUseCase = mocks.observeActiveUseCase,
                observeWaitingMatchesUseCase = mocks.observeWaitingUseCase,
                createMatchUseCase = mocks.createMatchUseCase,
                joinMatchUseCase = mocks.joinMatchUseCase,
                navigator = mocks.nav,
                sharingStarted = SharingStarted.Eagerly,
            )

        test("init should sign in if not authenticated") {
            val mocks = setupMocks()
            every { mocks.authRepo.uid } returns null andThen "test-uid"

            setupViewModel(mocks)
            advanceUntilIdle()

            coVerify { mocks.signInUseCase() }
        }

        test("uiState should combine active and waiting matches correctly") {
            val mocks = setupMocks()
            val match1 = Match("m1", "Match 1", "user1", null, "p1", null, "fen1", MatchStatus.ONGOING, 0L)
            val match2 = Match("m2", "Match 2", "user2", null, "p2", null, "fen2", MatchStatus.ONGOING, 0L)

            mocks.activeMatchesFlow.value = listOf(match1)
            mocks.waitingMatchesFlow.value = listOf(match2)

            val viewModel = setupViewModel(mocks)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            state.activeMatches shouldBe listOf(match1)
            state.waitingMatches shouldBe listOf(match2)
        }

        test("onCreateMatch should create match and navigate") {
            val mocks = setupMocks()
            coEvery { mocks.createMatchUseCase("My Match") } returns "new-match-id"

            val viewModel = setupViewModel(mocks)
            advanceUntilIdle()

            viewModel.onCreateMatch("My Match")
            advanceUntilIdle()

            coVerify { mocks.createMatchUseCase("My Match") }
            verify { mocks.nav.navigateToMatch("new-match-id") }
        }

        test("onJoinMatch should join match and navigate if successful") {
            val mocks = setupMocks()
            coEvery { mocks.joinMatchUseCase("m1") } returns true

            val viewModel = setupViewModel(mocks)
            advanceUntilIdle()

            viewModel.onJoinMatch("m1")
            advanceUntilIdle()

            coVerify { mocks.joinMatchUseCase("m1") }
            verify { mocks.nav.navigateToMatch("m1") }
        }

        test("onJoinMatch should not navigate if joining fails") {
            val mocks = setupMocks()
            coEvery { mocks.joinMatchUseCase("m1") } returns false

            val viewModel = setupViewModel(mocks)
            advanceUntilIdle()

            viewModel.onJoinMatch("m1")
            advanceUntilIdle()

            verify(exactly = 0) { mocks.nav.navigateToMatch(any()) }
        }

        test("onBack should navigate back") {
            val mocks = setupMocks()
            val viewModel = setupViewModel(mocks)
            advanceUntilIdle()

            viewModel.onBack()

            verify { mocks.nav.navigateBack() }
        }
    })
