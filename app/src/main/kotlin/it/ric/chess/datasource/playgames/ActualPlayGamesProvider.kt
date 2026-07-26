package it.ric.chess.datasource.playgames

import android.app.Activity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.PlayGames
import it.ric.chess.core.log.Logger
import it.ric.chess.core.log.tag
import it.ric.chess.core.log.withFixedTag
import it.ric.chess.core.model.PlayerInfo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class ActualPlayGamesProvider(
    private val activity: Activity,
    log: Logger,
) : PlayGamesProvider {
    private val log = log.withFixedTag(tag)

    override suspend fun getAuthCode(webClientId: String): String? =
        try {
            val gamesSignInClient = PlayGames.getGamesSignInClient(activity)
            val authResult = gamesSignInClient.isAuthenticated().await()
            if (authResult.isAuthenticated) {
                gamesSignInClient.requestServerSideAccess(webClientId, false).await()
            } else {
                val signInResult = gamesSignInClient.signIn().await()
                if (signInResult.isAuthenticated) {
                    gamesSignInClient.requestServerSideAccess(webClientId, false).await()
                } else {
                    log.debug("Sign-in failed or cancelled")
                    null
                }
            }
        } catch (e: ApiException) {
            log.error("API Error getting auth code: status=${e.statusCode}, message=${e.message}")
            null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.error("Error getting auth code", e)
            null
        }

    override suspend fun getPlayerInfo(): PlayerInfo? =
        try {
            val gamesSignInClient = PlayGames.getGamesSignInClient(activity)
            val authResult = gamesSignInClient.isAuthenticated().await()
            if (authResult.isAuthenticated) {
                val playersClient = PlayGames.getPlayersClient(activity)
                val player = playersClient.currentPlayer.await()
                PlayerInfo(
                    playerId = player.playerId,
                    displayName = player.displayName,
                    iconImageUri = player.iconImageUri,
                )
            } else {
                null
            }
        } catch (e: ApiException) {
            log.error("API Error getting player info: status=${e.statusCode}, message=${e.message}")
            null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.error("Error getting player info", e)
            null
        }

    override suspend fun getPlayerInfo(playerId: String): PlayerInfo? =
        try {
            val playersClient = PlayGames.getPlayersClient(activity)
            // loadPlayer returns Task<AnnotatedData<Player>>
            val annotatedData = playersClient.loadPlayer(playerId).await()
            val player = annotatedData.get()
            if (player != null) {
                PlayerInfo(
                    playerId = player.playerId,
                    displayName = player.displayName,
                    iconImageUri = player.iconImageUri,
                )
            } else {
                null
            }
        } catch (e: ApiException) {
            log.error("API Error loading player $playerId: status=${e.statusCode}, message=${e.message}")
            null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.error("Error loading player $playerId", e)
            null
        }

    override suspend fun signOut() {
        // In Play Games Services v2, sign-out is managed by the OS settings.
        // We don't need to (and can't) call an explicit sign-out here.
        log.debug("Play Games sign-out requested (no-op in v2)")
    }
}
