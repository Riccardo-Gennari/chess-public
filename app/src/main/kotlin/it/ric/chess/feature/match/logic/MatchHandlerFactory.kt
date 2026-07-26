package it.ric.chess.feature.match.logic

import it.ric.chess.repository.AuthRepository
import it.ric.chess.repository.MatchRepository
import it.ric.chess.repository.PlayerRepository
import javax.inject.Inject
import javax.inject.Singleton

interface MatchHandlerFactory {
    fun create(matchId: String?): MatchHandler
}

@Singleton
class DefaultMatchHandlerFactory @Inject constructor(
    private val matchRepository: MatchRepository,
    private val playerRepository: PlayerRepository,
    private val authRepository: AuthRepository,
) : MatchHandlerFactory {
    override fun create(matchId: String?): MatchHandler {
        return if (matchId != null) {
            RemoteMatchHandler(matchRepository, playerRepository, matchId, authRepository.uid)
        } else {
            LocalMatchHandler(matchRepository)
        }
    }
}
