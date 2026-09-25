package it.ric.chess.feature.menu

import dagger.hilt.android.lifecycle.HiltViewModel
import it.ric.chess.core.log.Logger
import it.ric.chess.core.viewmodel.BaseViewModel
import it.ric.chess.domain.model.PlayerInfo
import it.ric.chess.domain.usecase.auth.GetAuthenticatedPlayerUseCase
import it.ric.chess.domain.usecase.auth.SignInWithPlayGamesUseCase
import it.ric.chess.domain.usecase.auth.SignOutUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MenuViewModel
    @Inject
    constructor(
        log: Logger,
        getAuthenticatedPlayerUseCase: GetAuthenticatedPlayerUseCase,
        private val signInWithPlayGamesUseCase: SignInWithPlayGamesUseCase,
        private val signOutUseCase: SignOutUseCase,
        private val navigator: MenuNavigator,
        sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(5_000),
    ) : BaseViewModel(log) {

        private val playerInfo =
            getAuthenticatedPlayerUseCase()
                .stateIn(scope, sharingStarted, PlayerInfo.anonymous)

        val uiState =
            playerInfo.map { player ->
                MenuUiState(
                    isSignedIn = player.playerId.isNotEmpty(),
                    playerInfo = player,
                    loadingState = viewModelLoadingState,
                )
            }.stateIn(scope, sharingStarted, MenuUiState())

        fun onSelectSinglePlayer() {
            navigator.navigateToSinglePlayer()
        }

        fun onSelectMultiPlayer() {
            navigator.navigateToMultiplayerMenu()
        }

        fun onSignIn() {
            scope.launchWhileLoadingIfIdle {
                signInWithPlayGamesUseCase()
            }
        }

        fun onSignOut() {
            scope.launchWhileLoadingIfIdle {
                signOutUseCase()
            }
        }
    }
