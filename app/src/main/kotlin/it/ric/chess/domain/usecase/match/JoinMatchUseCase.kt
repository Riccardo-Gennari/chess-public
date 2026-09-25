package it.ric.chess.domain.usecase.match

import it.ric.chess.domain.repository.AuthRepository
import it.ric.chess.domain.repository.MatchRepository
import it.ric.chess.domain.repository.PlayerRepository
import javax.inject.Inject

class JoinMatchUseCase @Inject constructor(
    private val matchRepository: MatchRepository,
    private val authRepository: AuthRepository,
    private val playerRepository: PlayerRepository,
) {
    suspend operator fun invoke(matchId: String): Boolean {
        val uid = authRepository.uid ?: return false
        val pgsId = playerRepository.getCurrentPlayerInfo()?.playerId
        return matchRepository.joinMatch(matchId, uid, pgsId)
    }
}
