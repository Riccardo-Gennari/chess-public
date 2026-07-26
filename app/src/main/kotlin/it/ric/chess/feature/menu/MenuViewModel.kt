package it.ric.chess.feature.menu

import dagger.hilt.android.lifecycle.HiltViewModel
import it.ric.chess.core.log.Logger
import it.ric.chess.core.model.PlayerInfo
import it.ric.chess.core.viewmodel.BaseViewModel
import it.ric.chess.repository.AuthRepository
import it.ric.chess.repository.PlayerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MenuViewModel
    @Inject
    constructor(
        log: Logger,
        private val authRepository: AuthRepository,
        private val playerRepository: PlayerRepository,
        private val navigator: MenuNavigator,
        sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(5_000),
    ) : BaseViewModel(log) {
        private val authUser =
            authRepository.authUser
                .distinctUntilChanged()
                .stateIn(scope, sharingStarted, null)

        private val playerInfo =
            authUser
                .mapLatest { user ->
                    if (user != null) {
                        playerRepository.getCurrentPlayerInfo() ?: PlayerInfo.anonymous
                    } else {
                        PlayerInfo.anonymous
                    }
                }.stateIn(scope, sharingStarted, PlayerInfo.anonymous)

        val uiState =
            combine(authUser, playerInfo) { authUser, playerInfo ->
                MenuUiState(
                    isSignedIn = authUser != null,
                    playerInfo = playerInfo,
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
                authRepository.signInWithPlayGames()
            }
        }

        fun onSignOut() {
            scope.launchWhileLoadingIfIdle {
                authRepository.signOutFromPlayGames()
            }
        }
    }
