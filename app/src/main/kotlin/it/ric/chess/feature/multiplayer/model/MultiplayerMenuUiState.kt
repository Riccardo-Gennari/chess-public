package it.ric.chess.feature.multiplayer.model

import androidx.compose.runtime.Immutable
import it.ric.chess.core.ui.model.UiText
import it.ric.chess.core.viewmodel.LoadingState
import it.ric.chess.domain.model.Match

@Immutable
data class MultiplayerMenuUiState(
    val activeMatches: List<Match> = emptyList(),
    val loadingState: LoadingState = LoadingState.Idle,
    val userMessage: UiText? = null,
    val waitingMatches: List<Match> = emptyList(),
)
