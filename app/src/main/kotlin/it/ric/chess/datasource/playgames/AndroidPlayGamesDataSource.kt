package it.ric.chess.datasource.playgames

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.PlayGames
import it.ric.chess.core.infrastructure.ActivityProvider
import it.ric.chess.core.log.Logger
import it.ric.chess.core.log.tag
import it.ric.chess.core.log.withFixedTag
import it.ric.chess.domain.model.PlayerInfo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Android implementation of [PlayGamesDataSource] that uses an [ActivityProvider]
 * to access the current activity context.
 */
class AndroidPlayGamesDataSource @Inject constructor(
    private val activityProvider: ActivityProvider,
    log: Logger,
) : PlayGamesDataSource {
    private val log = log.withFixedTag(tag)

    override suspend fun getAuthCode(webClientId: String): String? {
        val currentActivity = activityProvider.currentActivity ?: return null
        return try {
            val gamesSignInClient = PlayGames.getGamesSignInClient(currentActivity)
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
    }

    override suspend fun getPlayerInfo(): PlayerInfo? {
        val currentActivity = activityProvider.currentActivity ?: return null
        return try {
            val gamesSignInClient = PlayGames.getGamesSignInClient(currentActivity)
            val authResult = gamesSignInClient.isAuthenticated().await()
            if (authResult.isAuthenticated) {
                val playersClient = PlayGames.getPlayersClient(currentActivity)
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
    }

    override suspend fun getPlayerInfo(playerId: String): PlayerInfo? {
        val currentActivity = activityProvider.currentActivity ?: return null
        return try {
            val playersClient = PlayGames.getPlayersClient(currentActivity)
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
    }

    override suspend fun signOut() {
        // In Play Games Services v2, sign-out is managed by the OS settings.
        log.debug("Play Games sign-out requested (no-op in v2)")
    }
}
