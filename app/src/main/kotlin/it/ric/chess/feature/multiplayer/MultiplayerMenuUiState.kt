package it.ric.chess.feature.multiplayer

import androidx.compose.runtime.Immutable
import it.ric.chess.core.viewmodel.LoadingState
import it.ric.chess.domain.model.Match

@Immutable
data class MultiplayerMenuUiState(
    val activeMatches: List<Match> = emptyList(),
    val waitingMatches: List<Match> = emptyList(),
    val loadingState: LoadingState = LoadingState.Idle,
)
