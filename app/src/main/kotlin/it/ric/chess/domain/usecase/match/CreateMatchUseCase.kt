package it.ric.chess.domain.usecase.match

import it.ric.chess.domain.logic.toFen
import it.ric.chess.domain.model.initialBoard
import it.ric.chess.domain.repository.AuthRepository
import it.ric.chess.domain.repository.MatchRepository
import it.ric.chess.domain.repository.PlayerRepository
import javax.inject.Inject

class CreateMatchUseCase
    @Inject
    constructor(
        private val matchRepository: MatchRepository,
        private val authRepository: AuthRepository,
        private val playerRepository: PlayerRepository,
    ) {
        suspend operator fun invoke(name: String = ""): String? {
            val uid = authRepository.uid ?: return null
            val pgsId = playerRepository.getCurrentPlayerInfo()?.playerId
            return matchRepository.createMatch(
                uid = uid,
                initialFen = initialBoard().toFen(),
                name = name.ifBlank { null },
                whitePgsId = pgsId,
            )
        }
    }
