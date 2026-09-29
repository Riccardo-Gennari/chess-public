package it.ric.chess.data.datasource.playgames

import it.ric.chess.domain.model.PlayerInfo

interface PlayGamesProvider {
    suspend fun getAuthCode(webClientId: String): String?

    suspend fun getPlayerInfo(): PlayerInfo?

    suspend fun getPlayerInfo(playerId: String): PlayerInfo?

    suspend fun signOut()

    companion object {
        val noOp =
            object : PlayGamesProvider {
                override suspend fun getAuthCode(webClientId: String): String? = null

                override suspend fun getPlayerInfo(): PlayerInfo? = null

                override suspend fun getPlayerInfo(playerId: String): PlayerInfo? = null

                override suspend fun signOut() {}
            }
    }
}
