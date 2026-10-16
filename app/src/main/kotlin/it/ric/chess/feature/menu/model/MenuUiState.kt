package it.ric.chess.feature.menu.model

import androidx.compose.runtime.Immutable
import it.ric.chess.core.ui.component.UiConfirmationAlert
import it.ric.chess.core.ui.model.UiText
import it.ric.chess.core.viewmodel.LoadingState
import it.ric.chess.domain.model.PlayerInfo

@Immutable
data class MenuUiState(
    val isSignedIn: Boolean = false,
    val loadingState: LoadingState = LoadingState.Idle,
    val signInConfirmationAlert: UiConfirmationAlert? = null,
    val snackbarMessage: UiText? = null,
    val playerInfo: PlayerInfo? = null,
)
