package it.ric.chess.feature.match.model

import androidx.compose.runtime.Immutable
import it.ric.chess.core.model.GameMode
import it.ric.chess.core.model.MatchStatus
import it.ric.chess.core.util.UiText
import it.ric.chess.core.viewmodel.LoadingState

/**
 * UI state for the chess screen.
 * Uses immutable Board to trigger Compose recompositions on state changes.
 */
@Immutable
data class ChessUiState(
    val board: Board,
    val selected: Pair<Int, Int>? = null,
    val validMoves: Set<Pair<Int, Int>> = emptySet(),
    val currentTurn: PieceColor = PieceColor.WHITE,
    val gameStatus: MatchStatus = MatchStatus.ONGOING,
    val myColor: PieceColor? = null,
    val whitePlayerName: String? = null,
    val blackPlayerName: String? = null,
    val gameMode: GameMode = GameMode.LOCAL,
    val loadingState: LoadingState = LoadingState.Idle,
    val userMessage: UiText? = null,
)
