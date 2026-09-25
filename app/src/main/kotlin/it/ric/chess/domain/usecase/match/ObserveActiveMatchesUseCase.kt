package it.ric.chess.domain.usecase.match

import it.ric.chess.domain.model.Match
import it.ric.chess.domain.repository.AuthRepository
import it.ric.chess.domain.repository.MatchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveActiveMatchesUseCase @Inject constructor(
    private val matchRepository: MatchRepository,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<List<Match>> =
        authRepository.authUser.flatMapLatest { user ->
            if (user != null) {
                matchRepository.observeMyMatches(user.uid)
            } else {
                flowOf(emptyList())
            }
        }
}
