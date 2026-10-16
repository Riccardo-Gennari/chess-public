package it.ric.chess.data.datasource.playgames

import android.util.Log.e
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
class AndroidPlayGamesDataSource
    @Inject
    constructor(
        private val activityProvider: ActivityProvider,
        log: Logger,
    ) : PlayGamesDataSource {
        private val log = log.withFixedTag(tag)

        override suspend fun getAuthCode(webClientId: String): Result<String?> {
            val currentActivity = activityProvider.currentActivity
                ?: return Result.failure(IllegalStateException("No current activity"))

            return try {
                val gamesSignInClient = PlayGames.getGamesSignInClient(currentActivity)
                val authResult = gamesSignInClient.isAuthenticated().await()
                if (authResult.isAuthenticated) {
                    Result.success(gamesSignInClient.requestServerSideAccess(webClientId, false).await())
                } else {
                    val signInResult = gamesSignInClient.signIn().await()
                    if (signInResult.isAuthenticated) {
                        Result.success(gamesSignInClient.requestServerSideAccess(webClientId, false).await())
                    } else {
                        log.debug("Sign-in failed or cancelled")
                        Result.success(null)
                    }
                }
            } catch (e: ApiException) {
                log.error("API Error getting auth code: status=${e.statusCode}, message=${e.message}")
                Result.failure(e)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log.error("Error getting auth code", e)
                Result.failure(e)
            }
        }

        override suspend fun getPlayerInfo(): Result<PlayerInfo?> {
            val currentActivity = activityProvider.currentActivity
                ?: return Result.failure(IllegalStateException("No current activity"))

            return try {
                val gamesSignInClient = PlayGames.getGamesSignInClient(currentActivity)
                val authResult = gamesSignInClient.isAuthenticated().await()
                if (authResult.isAuthenticated) {
                    val playersClient = PlayGames.getPlayersClient(currentActivity)
                    val player = playersClient.currentPlayer.await()
                    Result.success(
                        PlayerInfo(
                            playerId = player.playerId,
                            displayName = player.displayName,
                            iconImageUri = player.iconImageUri?.toString(),
                        ),
                    )
                } else {
                    Result.success(null)
                }
            } catch (e: ApiException) {
                log.error("API Error getting player info: status=${e.statusCode}, message=${e.message}")
                Result.failure(e)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log.error("Error getting player info", e)
                Result.failure(e)
            }
        }

        override suspend fun getPlayerInfo(playerId: String): Result<PlayerInfo?> {
            val currentActivity = activityProvider.currentActivity
                ?: return Result.failure(IllegalStateException("No current activity"))
            return try {
                val playersClient = PlayGames.getPlayersClient(currentActivity)
                val annotatedData = playersClient.loadPlayer(playerId).await()
                val player = annotatedData.get()
                if (player != null) {
                    Result.success(
                        PlayerInfo(
                            playerId = player.playerId,
                            displayName = player.displayName,
                            iconImageUri = player.iconImageUri?.toString(),
                        ),
                    )
                } else {
                    Result.success(null)
                }
            } catch (e: ApiException) {
                log.error("API Error loading player $playerId: status=${e.statusCode}, message=${e.message}")
                Result.failure(e)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log.error("Error loading player $playerId", e)
                Result.failure(e)
            }
        }

        override suspend fun signOut() {
            // In Play Games Services v2, sign-out is managed by the OS settings.
            log.debug("Play Games sign-out requested (no-op in v2)")
        }
    }
