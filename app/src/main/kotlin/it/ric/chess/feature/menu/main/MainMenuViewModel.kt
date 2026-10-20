package it.ric.chess.feature.menu.main

import dagger.hilt.android.lifecycle.HiltViewModel
import it.ric.chess.R
import it.ric.chess.core.log.Logger
import it.ric.chess.core.ui.component.UiAlertAction
import it.ric.chess.core.ui.component.UiConfirmationAlert
import it.ric.chess.core.ui.model.UiText
import it.ric.chess.core.viewmodel.BaseViewModel
import it.ric.chess.domain.model.PlayerInfo
import it.ric.chess.domain.model.isSignedIn
import it.ric.chess.domain.usecase.auth.GetAuthenticatedPlayerUseCase
import it.ric.chess.domain.usecase.auth.SignInWithPlayGamesUseCase
import it.ric.chess.domain.usecase.auth.SignOutUseCase
import it.ric.chess.feature.menu.main.model.MainMenuUiState
import it.ric.chess.feature.menu.main.navigation.MainMenuNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel
    @Inject
    constructor(
        log: Logger,
        getAuthenticatedPlayerUseCase: GetAuthenticatedPlayerUseCase,
        private val signInWithPlayGamesUseCase: SignInWithPlayGamesUseCase,
        private val signOutUseCase: SignOutUseCase,
        private val navigator: MainMenuNavigator,
        sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(),
    ) : BaseViewModel(log) {
        private val signInConfirmation = MutableStateFlow<UiConfirmationAlert?>(null)
        private val snackbarMessage = MutableStateFlow<UiText?>(null)
        private val playerInfo =
            getAuthenticatedPlayerUseCase()
                .onEach { if (it.isFailure) snackbarMessage.value = UiText.Resource(R.string.error_sign_in) }
                .map { it.getOrNull() ?: PlayerInfo.guest }
                .stateIn(scope, sharingStarted, null)

        val uiState = combine(
            playerInfo,
            signInConfirmation,
            snackbarMessage,
        ) { playerInfo, signInConfirmation, snackbarMessage ->
            MainMenuUiState(
                isSignedIn = !playerInfo?.playerId.isNullOrBlank(),
                loadingState = viewModelLoadingState,
                playerInfo = playerInfo,
                signInConfirmationAlert = signInConfirmation,
                snackbarMessage = snackbarMessage,
            )
        }.stateIn(scope, sharingStarted, MainMenuUiState())

        init {
            scope.launchWhileLoading {
                uiState.first { it.playerInfo != null } // Wait for player info
            }
        }

        fun onDismissMessage() {
            snackbarMessage.value = null
        }

        fun onSelectSinglePlayer() {
            navigator.navigateToSinglePlayer()
        }

        fun onSelectMultiPlayer() {
            if (!playerInfo.value.isSignedIn) {
                signInConfirmation.value = UiConfirmationAlert(
                    title = UiText.Resource(R.string.sign_in_required),
                    message = UiText.Resource(R.string.sign_in_to_continue),
                    positiveAction = UiAlertAction(UiText.Resource(android.R.string.ok)) {
                        signInConfirmation.value = null
                        onSignIn()
                    },
                    negativeAction = UiAlertAction(UiText.Resource(android.R.string.cancel)) {
                        signInConfirmation.value = null
                    },
                )
            } else {
                navigator.navigateToMultiplayerMenu()
            }
        }

        fun onSignIn() {
            scope.launchWhileLoadingIfIdle {
                signInWithPlayGamesUseCase().onFailure {
                    snackbarMessage.value = UiText.Resource(R.string.error_sign_in)
                }
            }
        }

        fun onSignOut() {
            scope.launchWhileLoadingIfIdle {
                signOutUseCase().onFailure {
                    snackbarMessage.value = UiText.Resource(R.string.error_sign_out)
                }
            }
        }
    }
