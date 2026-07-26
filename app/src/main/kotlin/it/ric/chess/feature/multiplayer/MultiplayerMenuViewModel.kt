package it.ric.chess.feature.multiplayer

import dagger.hilt.android.lifecycle.HiltViewModel
import it.ric.chess.core.log.Logger
import it.ric.chess.core.model.Match
import it.ric.chess.core.viewmodel.BaseViewModel
import it.ric.chess.feature.match.logic.toFen
import it.ric.chess.feature.match.model.initialBoard
import it.ric.chess.repository.AuthRepository
import it.ric.chess.repository.MatchRepository
import it.ric.chess.repository.PlayerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MultiplayerMenuViewModel
    @Inject
    constructor(
        log: Logger,
        private val authRepository: AuthRepository,
        private val matchRepository: MatchRepository,
        private val playerRepository: PlayerRepository,
        private val navigator: MultiplayerMenuNavigator,
        sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(5000),
    ) : BaseViewModel(log) {
        init {
            scope.launchWhileLoading {
                if (authRepository.uid == null) {
                    authRepository.signInWithPlayGames()
                    if (authRepository.uid == null) {
                        // Sign in failed or canceled
                        navigator.navigateBack()
                    }
                }
            }
        }

        private val activeMatches: StateFlow<List<Match>> =
            authRepository.authUser
                .flatMapLatest { user ->
                    if (user != null) {
                        matchRepository.observeMyMatches(user.uid)
                    } else {
                        flowOf(emptyList())
                    }
                }.catch { e ->
                    viewModelLog.error("Failed to observe my matches", e)
                    emit(emptyList())
                }.stateIn(scope, sharingStarted, emptyList())

        private val waitingMatches: StateFlow<List<Match>> =
            combine(
                matchRepository.observeWaitingMatches(),
                activeMatches,
            ) { waiting, active ->
                val activeIds = active.map { it.id }.toSet()
                waiting.filter { it.id !in activeIds }
            }.catch { e ->
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
                    val uid = authRepository.uid ?: return@launchWhileLoadingIfIdle
                    val pgsId = playerRepository.getCurrentPlayerInfo()?.playerId
                    val matchId =
                        matchRepository.createMatch(
                            uid = uid,
                            initialFen = initialBoard().toFen(),
                            name = name.ifBlank { null },
                            whitePgsId = pgsId,
                        )
                    navigator.navigateToMatch(matchId)
                } catch (e: Exception) {
                    viewModelLog.error("Failed to create match", e)
                }
            }
        }

        fun onJoinMatch(matchId: String) {
            scope.launchWhileLoadingIfIdle {
                try {
                    val uid = authRepository.uid ?: return@launchWhileLoadingIfIdle
                    val pgsId = playerRepository.getCurrentPlayerInfo()?.playerId
                    if (matchRepository.joinMatch(matchId, uid, pgsId)) {
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
