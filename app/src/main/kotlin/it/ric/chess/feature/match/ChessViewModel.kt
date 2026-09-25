package it.ric.chess.feature.match

import androidx.compose.runtime.Immutable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import it.ric.chess.R
import it.ric.chess.core.log.Logger
import it.ric.chess.core.util.UiText
import it.ric.chess.core.viewmodel.BaseViewModel
import it.ric.chess.domain.handler.MatchHandler
import it.ric.chess.domain.handler.MatchHandlerFactory
import it.ric.chess.domain.model.Board
import it.ric.chess.domain.model.GameMode
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.model.copy
import it.ric.chess.domain.model.initialBoard
import it.ric.chess.domain.usecase.auth.GetAuthenticatedPlayerUseCase
import it.ric.chess.domain.usecase.match.CalculateValidMovesUseCase
import it.ric.chess.domain.usecase.match.ExecuteMoveUseCase
import it.ric.chess.feature.match.model.ChessUiState
import it.ric.chess.feature.match.model.ChessboardNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Multiplatform view model managing chess state reactively.
 */
@HiltViewModel(assistedFactory = ChessViewModel.Factory::class)
class ChessViewModel
    @AssistedInject
    constructor(
        log: Logger,
        private val navigator: ChessboardNavigator,
        getAuthenticatedPlayerUseCase: GetAuthenticatedPlayerUseCase,
        private val calculateValidMovesUseCase: CalculateValidMovesUseCase,
        private val executeMoveUseCase: ExecuteMoveUseCase,
        matchHandlerFactory: MatchHandlerFactory,
        @Assisted private val matchId: String?,
        sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(5_000),
    ) : BaseViewModel(log) {
        private val matchHandler: MatchHandler = matchHandlerFactory.create(matchId)

        @AssistedFactory
        interface Factory {
            fun create(matchId: String?): ChessViewModel
        }

        @Immutable
        private data class GameState(
            val board: Board,
            val turn: PieceColor = PieceColor.WHITE,
            val status: MatchStatus = MatchStatus.ONGOING,
            val playerColor: PieceColor? = null,
            val whitePlayerName: String? = null,
            val blackPlayerName: String? = null,
        )

        private val gameStateFlow =
            matchHandler
                .observeState()
                .map { update ->
                    GameState(
                        board = update.board,
                        turn = update.turn,
                        status = update.status,
                        playerColor = update.playerColor,
                        whitePlayerName = update.whitePlayerName,
                        blackPlayerName = update.blackPlayerName,
                    )
                }.catch { e ->
                    viewModelLog.error("Error observing match state", e)
                }.stateIn(
                    scope = scope,
                    started = sharingStarted,
                    initialValue = GameState(board = initialBoard().copy()),
                )

        private val selectedFlow = MutableStateFlow<Pair<Int, Int>?>(null)
        private val userMessageFlow = MutableStateFlow<UiText?>(null)

        private val playerNameFlow =
            getAuthenticatedPlayerUseCase()
                .map { it.displayName }
                .stateIn(scope, sharingStarted, null)

        // Derived valid moves based on current board and selection
        private val validMovesFlow: StateFlow<Set<Pair<Int, Int>>> =
            combine(gameStateFlow, selectedFlow) { state, selected ->
                if (selected == null) return@combine emptySet()
                val (row, col) = selected
                val piece = state.board[row][col] ?: return@combine emptySet()

                // In multiplayer, only calculate moves if it's the player's turn
                if (matchHandler.gameMode == GameMode.REMOTE && state.playerColor != null && piece.color != state.playerColor) {
                    return@combine emptySet()
                }

                calculateValidMovesUseCase(state.board, row, col, piece)
            }.stateIn(scope, sharingStarted, emptySet())

        // Final UI state derived from all source flows
        val uiState: StateFlow<ChessUiState> =
            combine(
                gameStateFlow,
                selectedFlow,
                validMovesFlow,
                playerNameFlow,
                userMessageFlow,
            ) { state, selected, validMoves, playerName, userMessage ->
                ChessUiState(
                    board = state.board,
                    selected = selected,
                    validMoves = validMoves,
                    currentTurn = state.turn,
                    gameStatus = state.status,
                    myColor = state.playerColor,
                    whitePlayerName =
                        state.whitePlayerName
                            ?: if (state.playerColor == PieceColor.WHITE || matchHandler.gameMode == GameMode.LOCAL) playerName else null,
                    blackPlayerName =
                        state.blackPlayerName
                            ?: if (state.playerColor == PieceColor.BLACK || matchHandler.gameMode == GameMode.LOCAL) playerName else null,
                    gameMode = matchHandler.gameMode,
                    loadingState = viewModelLoadingState,
                    userMessage = userMessage,
                )
            }.stateIn(
                scope,
                sharingStarted,
                ChessUiState(board = initialBoard().copy()),
            )

        fun onBack() {
            navigator.navigateBack()
        }

        fun onUserMessageShown() {
            userMessageFlow.value = null
        }

        fun quitMatch() {
            scope.launchWhileLoadingIfIdle {
                try {
                    matchHandler.onQuit()
                } catch (e: Exception) {
                    viewModelLog.error("Failed to quit match", e)
                }
                navigator.navigateBack()
            }
        }

        fun reset() {
            scope.launchWhileLoadingIfIdle {
                try {
                    matchHandler.onReset()
                } catch (e: Exception) {
                    viewModelLog.error("Failed to reset match", e)
                }
                selectedFlow.value = null
            }
        }

        // Select or move when a cell is clicked
        fun onCellClicked(
            row: Int,
            col: Int,
        ) {
            val state = gameStateFlow.value
            if (state.status != MatchStatus.ONGOING) return

            val board = state.board
            val piece = board[row][col]
            val selected = selectedFlow.value
            val turn = state.turn

            // In multiplayer, check if it's the player's turn
            val playerColor = state.playerColor
            if (matchHandler.gameMode == GameMode.REMOTE && (playerColor == null || turn != playerColor)) {
                userMessageFlow.value = UiText.StringResource(R.string.not_your_turn)
                return
            }

            // If nothing selected and there's a piece of current turn -> select
            if (selected == null) {
                if (piece != null && piece.color == turn) {
                    selectedFlow.value = Pair(row, col)
                }
                return
            }

            // If clicked same square -> deselect
            if (selected.first == row && selected.second == col) {
                selectedFlow.value = null
                return
            }

            // If clicked another piece of current turn -> select it instead
            if (piece != null && piece.color == turn) {
                selectedFlow.value = Pair(row, col)
                return
            }

            // Attempt move from selected -> clicked
            tryMove(selected.first, selected.second, row, col)
        }

        private fun tryMove(
            fromRow: Int,
            fromCol: Int,
            toRow: Int,
            toCol: Int,
        ) {
            val state = gameStateFlow.value
            val board = state.board
            val turn = state.turn

            val piece = board[fromRow][fromCol] ?: return

            // Enforce turn and valid moves
            if (piece.color != turn || !validMovesFlow.value.contains(Pair(toRow, toCol))) {
                selectedFlow.value = null
                userMessageFlow.value = UiText.StringResource(R.string.invalid_move)
                return
            }

            // Perform move and update state via Use Case
            val moveResult = executeMoveUseCase(board, fromRow, fromCol, toRow, toCol, piece, turn)

            scope.launchWhileLoadingIfIdle {
                try {
                    matchHandler.onMove(moveResult.nextBoard, moveResult.nextTurn, moveResult.nextStatus)
                } catch (e: Exception) {
                    viewModelLog.error("Failed to update move", e)
                }
            }
            selectedFlow.value = null
        }
    }
