package it.ric.chess.feature.menu.main.model

import androidx.compose.runtime.Immutable
import it.ric.chess.core.ui.component.UiConfirmationAlert
import it.ric.chess.core.ui.model.UiText
import it.ric.chess.core.viewmodel.LoadingState
import it.ric.chess.domain.model.PlayerInfo

@Immutable
data class MainMenuUiState(
    val isSignedIn: Boolean = false,
    val loadingState: LoadingState = LoadingState.Idle,
    val playerInfo: PlayerInfo? = null,
    val signInConfirmationAlert: UiConfirmationAlert? = null,
    val snackbarMessage: UiText? = null,
)
