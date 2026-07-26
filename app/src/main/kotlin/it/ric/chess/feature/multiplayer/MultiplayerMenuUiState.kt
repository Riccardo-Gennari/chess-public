package it.ric.chess.feature.multiplayer

import androidx.compose.runtime.Immutable
import it.ric.chess.core.model.Match
import it.ric.chess.core.viewmodel.LoadingState

@Immutable
data class MultiplayerMenuUiState(
    val activeMatches: List<Match> = emptyList(),
    val waitingMatches: List<Match> = emptyList(),
    val loadingState: LoadingState = LoadingState.Idle,
)
