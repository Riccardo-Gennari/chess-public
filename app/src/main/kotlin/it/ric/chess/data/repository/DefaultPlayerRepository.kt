package it.ric.chess.data.repository

import it.ric.chess.datasource.playgames.PlayGamesDataSource
import it.ric.chess.domain.model.PlayerInfo
import it.ric.chess.domain.repository.PlayerRepository
import javax.inject.Inject

/**
 * Implementation of [PlayerRepository] using [PlayGamesDataSource].
 */
class DefaultPlayerRepository @Inject constructor(
    private val dataSource: PlayGamesDataSource,
) : PlayerRepository {
    override suspend fun getCurrentPlayerInfo(): PlayerInfo? = dataSource.getPlayerInfo()

    override suspend fun getPlayerInfo(playerId: String): PlayerInfo? = dataSource.getPlayerInfo(playerId)
}
