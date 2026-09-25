package it.ric.chess.feature.menu

import androidx.compose.runtime.Immutable
import it.ric.chess.core.viewmodel.LoadingState
import it.ric.chess.domain.model.PlayerInfo

@Immutable
data class MenuUiState(
    val isSignedIn: Boolean = false,
    val playerInfo: PlayerInfo? = null,
    val loadingState: LoadingState = LoadingState.Idle,
)
