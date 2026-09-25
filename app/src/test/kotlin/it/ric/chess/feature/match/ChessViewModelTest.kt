package it.ric.chess.feature.match

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.ric.chess.core.log.Logger
import it.ric.chess.domain.handler.MatchHandler
import it.ric.chess.domain.handler.MatchHandlerFactory
import it.ric.chess.domain.handler.MatchStateUpdate
import it.ric.chess.domain.model.Board
import it.ric.chess.domain.model.GameMode
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.model.PlayerInfo
import it.ric.chess.domain.model.initialBoard
import it.ric.chess.domain.usecase.auth.GetAuthenticatedPlayerUseCase
import it.ric.chess.domain.usecase.match.CalculateValidMovesUseCase
import it.ric.chess.domain.usecase.match.ExecuteMoveUseCase
import it.ric.chess.feature.match.model.ChessboardNavigator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
class ChessViewModelTest :
    FunSpec({

        fun setupMocks(): Pair<Logger, ChessboardNavigator> {
            val mockLogger = mockk<Logger>(relaxed = true)
            val navigator = mockk<ChessboardNavigator>(relaxed = true)
            return mockLogger to navigator
        }

        fun setupViewModel(
            log: Logger,
            nav: ChessboardNavigator,
            handler: MatchHandler,
        ): ChessViewModel {
            val mockGetAuthPlayerUseCase = mockk<GetAuthenticatedPlayerUseCase> {
                every { this@mockk.invoke() } returns flowOf(PlayerInfo.anonymous)
            }
            val mockCalculateValidMovesUseCase = CalculateValidMovesUseCase()
            val mockExecuteMoveUseCase = ExecuteMoveUseCase()
            val mockFactory = mockk<MatchHandlerFactory> {
                every { create(any()) } returns handler
            }
            return ChessViewModel(
                log = log,
                navigator = nav,
                getAuthenticatedPlayerUseCase = mockGetAuthPlayerUseCase,
                calculateValidMovesUseCase = mockCalculateValidMovesUseCase,
                executeMoveUseCase = mockExecuteMoveUseCase,
                matchHandlerFactory = mockFactory,
                matchId = null,
                sharingStarted = SharingStarted.Eagerly,
            )
        }

        fun mockHandler(
            board: Board = initialBoard(),
            turn: PieceColor = PieceColor.WHITE,
            status: MatchStatus = MatchStatus.ONGOING,
        ): MatchHandler =
            mockk(relaxed = true) {
                every { observeState() } returns
                    MutableStateFlow(
                        MatchStateUpdate(
                            board = board,
                            turn = turn,
                            status = status,
                        ),
                    )
                every { gameMode } returns GameMode.LOCAL
            }

        test("initial state should be correct") {
            val (log, nav) = setupMocks()
            val handler = mockHandler()
            val viewModel = setupViewModel(log, nav, handler)

            advanceUntilIdle()

            val state = viewModel.uiState.value
            state.gameStatus shouldBe MatchStatus.ONGOING
            state.currentTurn shouldBe PieceColor.WHITE
            state.selected shouldBe null
            state.validMoves shouldBe emptySet()
        }

        test("onCellClicked should select a piece") {
            val (log, nav) = setupMocks()
            val handler = mockHandler()
            val viewModel = setupViewModel(log, nav, handler)

            advanceUntilIdle()

            // White pawn at (6, 0)
            viewModel.onCellClicked(6, 0)
            advanceUntilIdle()

            viewModel.uiState.value.selected shouldBe Pair(6, 0)
            viewModel.uiState.value.validMoves shouldNotBe emptySet<Pair<Int, Int>>()
        }

        test("onCellClicked should deselect when clicking same square") {
            val (log, nav) = setupMocks()
            val handler = mockHandler()
            val viewModel = setupViewModel(log, nav, handler)

            advanceUntilIdle()

            viewModel.onCellClicked(6, 0)
            advanceUntilIdle()
            viewModel.uiState.value.selected shouldBe Pair(6, 0)

            viewModel.onCellClicked(6, 0)
            advanceUntilIdle()
            viewModel.uiState.value.selected shouldBe null
        }

        test("onCellClicked should attempt move when clicking valid destination") {
            val (log, nav) = setupMocks()
            val handler = mockHandler()
            val viewModel = setupViewModel(log, nav, handler)

            advanceUntilIdle()

            // Select white pawn
            viewModel.onCellClicked(6, 0)
            advanceUntilIdle()

            // Click valid move (5, 0)
            viewModel.onCellClicked(5, 0)
            advanceUntilIdle()

            coVerify { handler.onMove(any(), any(), any()) }
            viewModel.uiState.value.selected shouldBe null
        }

        test("quitMatch should call handler and navigate back") {
            val (log, nav) = setupMocks()
            val handler = mockHandler()
            val viewModel = setupViewModel(log, nav, handler)

            viewModel.quitMatch()
            advanceUntilIdle()

            coVerify { handler.onQuit() }
            verify { nav.navigateBack() }
        }

        test("reset should call handler and clear selection") {
            val (log, nav) = setupMocks()
            val handler = mockHandler()
            val viewModel = setupViewModel(log, nav, handler)

            viewModel.onCellClicked(6, 0)
            advanceUntilIdle()

            viewModel.reset()
            advanceUntilIdle()

            coVerify { handler.onReset() }
            viewModel.uiState.value.selected shouldBe null
        }

        test("onBack should navigate back") {
            val (log, nav) = setupMocks()
            val handler = mockHandler()
            val viewModel = setupViewModel(log, nav, handler)

            viewModel.onBack()
            verify { nav.navigateBack() }
        }
    })
