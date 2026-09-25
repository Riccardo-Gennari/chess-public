package it.ric.chess.domain.usecase.match

import it.ric.chess.domain.model.Match
import it.ric.chess.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveWaitingMatchesUseCase @Inject constructor(
    private val matchRepository: MatchRepository,
    private val observeActiveMatchesUseCase: ObserveActiveMatchesUseCase,
) {
    operator fun invoke(): Flow<List<Match>> =
        combine(
            matchRepository.observeWaitingMatches(),
            observeActiveMatchesUseCase(),
        ) { waiting, active ->
            val activeIds = active.map { it.id }.toSet()
            waiting.filter { it.id !in activeIds }
        }
}
