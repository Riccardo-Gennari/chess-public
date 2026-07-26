package it.ric.chess.feature.multiplayer

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.ric.chess.core.log.Logger
import it.ric.chess.core.model.AuthUser
import it.ric.chess.core.model.Match
import it.ric.chess.core.model.MatchStatus
import it.ric.chess.core.model.PlayerInfo
import it.ric.chess.feature.match.advanceUntilIdle
import it.ric.chess.repository.AuthRepository
import it.ric.chess.repository.MatchRepository
import it.ric.chess.repository.PlayerRepository
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
            val matchRepo: MatchRepository,
            val playerRepo: PlayerRepository,
            val authUserFlow: MutableStateFlow<AuthUser?>,
            val waitingMatchesFlow: MutableStateFlow<List<Match>>,
            val activeMatchesFlow: MutableStateFlow<List<Match>>,
        )

        fun setupMocks(): Mocks {
            val authUserFlow = MutableStateFlow<AuthUser?>(null)
            val waitingMatchesFlow = MutableStateFlow<List<Match>>(emptyList())
            val activeMatchesFlow = MutableStateFlow<List<Match>>(emptyList())

            val authRepo =
                mockk<AuthRepository>(relaxed = true) {
                    every { authUser } returns authUserFlow
                    every { uid } answers { authUserFlow.value?.uid }
                    coEvery { signInWithPlayGames() } coAnswers {
                        authUserFlow.value = AuthUser("test-uid")
                    }
                }
            val matchRepo =
                mockk<MatchRepository>(relaxed = true) {
                    every { observeWaitingMatches() } returns waitingMatchesFlow
                    every { observeMyMatches(any()) } returns activeMatchesFlow
                }
            val playerRepo =
                mockk<PlayerRepository>(relaxed = true) {
                    coEvery { getCurrentPlayerInfo() } returns PlayerInfo("pgs-id", "Test Player")
                }
            val log = mockk<Logger>(relaxed = true)
            val nav = mockk<MultiplayerMenuNavigator>(relaxed = true)

            return Mocks(
                log,
                nav,
                authRepo,
                matchRepo,
                playerRepo,
                authUserFlow,
                waitingMatchesFlow,
                activeMatchesFlow,
            )
        }

        fun setupViewModel(mocks: Mocks) = MultiplayerMenuViewModel(
            log = mocks.log,
            authRepository = mocks.authRepo,
            matchRepository = mocks.matchRepo,
            playerRepository = mocks.playerRepo,
            navigator = mocks.nav,
            sharingStarted = SharingStarted.Eagerly,
        )

        test("init should sign in if not authenticated") {
            val mocks = setupMocks()
            setupViewModel(mocks)

            advanceUntilIdle()

            coVerify { mocks.authRepo.signInWithPlayGames() }
            mocks.authUserFlow.value?.uid shouldBe "test-uid"
        }

        test("init should navigate back if sign in fails") {
            val mocks = setupMocks()
            coEvery { mocks.authRepo.signInWithPlayGames() } returns Unit // Doesn't update authUserFlow

            setupViewModel(mocks)

            advanceUntilIdle()

            verify { mocks.nav.navigateBack() }
        }

        test("uiState should combine active and waiting matches correctly") {
            val mocks = setupMocks()
            mocks.authUserFlow.value = AuthUser("user1")

            val match1 = Match("m1", "Match 1", "user1", null, "p1", null, "fen1", MatchStatus.ONGOING, 0L)
            val match2 = Match("m2", "Match 2", "user2", null, "p2", null, "fen2", MatchStatus.ONGOING, 0L)

            mocks.activeMatchesFlow.value = listOf(match1)
            mocks.waitingMatchesFlow.value = listOf(match1, match2)

            val viewModel = setupViewModel(mocks)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            state.activeMatches shouldBe listOf(match1)
            // waitingMatches should exclude active matches
            state.waitingMatches shouldBe listOf(match2)
        }

        test("onCreateMatch should create match and navigate") {
            val mocks = setupMocks()
            mocks.authUserFlow.value = AuthUser("user1")
            coEvery { mocks.matchRepo.createMatch(any(), any(), any(), any()) } returns "new-match-id"

            val viewModel = setupViewModel(mocks)
            advanceUntilIdle()

            viewModel.onCreateMatch("My Match")
            advanceUntilIdle()

            coVerify {
                mocks.matchRepo.createMatch(
                    uid = "user1",
                    initialFen = any(),
                    name = "My Match",
                    whitePgsId = "pgs-id",
                )
            }
            verify { mocks.nav.navigateToMatch("new-match-id") }
        }

        test("onJoinMatch should join match and navigate if successful") {
            val mocks = setupMocks()
            mocks.authUserFlow.value = AuthUser("user1")
            coEvery { mocks.matchRepo.joinMatch("m1", "user1", "pgs-id") } returns true

            val viewModel = setupViewModel(mocks)
            advanceUntilIdle()

            viewModel.onJoinMatch("m1")
            advanceUntilIdle()

            coVerify { mocks.matchRepo.joinMatch("m1", "user1", "pgs-id") }
            verify { mocks.nav.navigateToMatch("m1") }
        }

        test("onJoinMatch should not navigate if joining fails") {
            val mocks = setupMocks()
            mocks.authUserFlow.value = AuthUser("user1")
            coEvery { mocks.matchRepo.joinMatch("m1", "user1", "pgs-id") } returns false

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
