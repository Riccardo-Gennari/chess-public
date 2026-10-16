package it.ric.chess.data.datasource.playgames

import it.ric.chess.domain.model.PlayerInfo

interface PlayGamesDataSource {
    suspend fun getAuthCode(webClientId: String): Result<String?>

    suspend fun getPlayerInfo(): Result<PlayerInfo?>

    suspend fun getPlayerInfo(playerId: String): Result<PlayerInfo?>

    suspend fun signOut()
}
