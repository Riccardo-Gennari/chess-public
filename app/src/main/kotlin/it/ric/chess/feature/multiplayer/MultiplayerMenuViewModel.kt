package it.ric.chess.feature.multiplayer

import dagger.hilt.android.lifecycle.HiltViewModel
import it.ric.chess.core.log.Logger
import it.ric.chess.core.viewmodel.BaseViewModel
import it.ric.chess.domain.model.Match
import it.ric.chess.domain.repository.AuthRepository
import it.ric.chess.domain.usecase.auth.SignInWithPlayGamesUseCase
import it.ric.chess.domain.usecase.match.CreateMatchUseCase
import it.ric.chess.domain.usecase.match.JoinMatchUseCase
import it.ric.chess.domain.usecase.match.ObserveActiveMatchesUseCase
import it.ric.chess.domain.usecase.match.ObserveWaitingMatchesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MultiplayerMenuViewModel
    @Inject
    constructor(
        log: Logger,
        authRepository: AuthRepository,
        signInWithPlayGamesUseCase: SignInWithPlayGamesUseCase,
        observeActiveMatchesUseCase: ObserveActiveMatchesUseCase,
        observeWaitingMatchesUseCase: ObserveWaitingMatchesUseCase,
        private val createMatchUseCase: CreateMatchUseCase,
        private val joinMatchUseCase: JoinMatchUseCase,
        private val navigator: MultiplayerMenuNavigator,
        sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(5000),
    ) : BaseViewModel(log) {
        init {
            scope.launchWhileLoading {
                if (authRepository.uid == null) {
                    signInWithPlayGamesUseCase()
                    if (authRepository.uid == null) {
                        navigator.navigateBack()
                    }
                }
            }
        }

        private val activeMatches: StateFlow<List<Match>> =
            observeActiveMatchesUseCase()
                .catch { e ->
                    viewModelLog.error("Failed to observe my matches", e)
                    emit(emptyList())
                }.stateIn(scope, sharingStarted, emptyList())

        private val waitingMatches: StateFlow<List<Match>> =
            observeWaitingMatchesUseCase()
                .catch { e ->
                    viewModelLog.error("Failed to observe matches", e)
                    emit(emptyList())
                }.stateIn(scope, sharingStarted, emptyList())

        val uiState: StateFlow<MultiplayerMenuUiState> =
            combine(activeMatches, waitingMatches) { active, waiting ->
                MultiplayerMenuUiState(
                    activeMatches = active,
                    waitingMatches = waiting,
                    loadingState = viewModelLoadingState,
                )
            }.stateIn(scope, sharingStarted, MultiplayerMenuUiState())

        fun onCreateMatch(name: String = "") {
            scope.launchWhileLoadingIfIdle {
                try {
                    val matchId = createMatchUseCase(name)
                    if (matchId != null) {
                        navigator.navigateToMatch(matchId)
                    }
                } catch (e: Exception) {
                    viewModelLog.error("Failed to create match", e)
                }
            }
        }

        fun onJoinMatch(matchId: String) {
            scope.launchWhileLoadingIfIdle {
                try {
                    if (joinMatchUseCase(matchId)) {
                        navigator.navigateToMatch(matchId)
                    }
                } catch (e: Exception) {
                    viewModelLog.error("Failed to join match", e)
                }
            }
        }

        fun onBack() {
            navigator.navigateBack()
        }
    }
