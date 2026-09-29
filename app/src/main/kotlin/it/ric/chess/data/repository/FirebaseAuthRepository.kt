package it.ric.chess.data.repository

import com.google.firebase.auth.PlayGamesAuthProvider
import it.ric.chess.core.log.Logger
import it.ric.chess.core.log.tag
import it.ric.chess.core.log.withFixedTag
import it.ric.chess.datasource.datastore.UserPreferencesDataSource
import it.ric.chess.datasource.firebase.Firebase
import it.ric.chess.datasource.playgames.PlayGamesDataSource
import it.ric.chess.domain.model.AuthUser
import it.ric.chess.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named

class FirebaseAuthRepository
    @Inject
    constructor(
        private val userPreferencesDataSource: UserPreferencesDataSource,
        private val firebase: Firebase,
        private val playGamesDataSource: PlayGamesDataSource,
        log: Logger,
        @Named("webClientId") private val webClientId: String,
    ) : AuthRepository {
        override val uid: String? get() = firebase.uid
        override val authUser: Flow<AuthUser?> = firebase.observeAuthState()
        private val log = log.withFixedTag(tag)

        override val isPlayGamesAuthAvailable: Flow<Boolean> = userPreferencesDataSource.isPlayGamesAuthAvailable

        override suspend fun signInWithPlayGames() {
            try {
                val serverAuthCode = playGamesDataSource.getAuthCode(webClientId)
                if (serverAuthCode != null) {
                    val credential = PlayGamesAuthProvider.getCredential(serverAuthCode)
                    firebase.signInWithCredential(credential)
                    log.debug("Signed in with Play Games: $uid")
                    userPreferencesDataSource.setPlayGamesAuthAvailable(true)
                } else {
                    userPreferencesDataSource.setPlayGamesAuthAvailable(false)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log.debug("Failed to sign in with Play Games", e)
            }
        }

        override suspend fun signOutFromPlayGames() {
            try {
                playGamesDataSource.signOut()
                userPreferencesDataSource.setPlayGamesAuthAvailable(false)
                firebase.signOut()
                log.debug("Signed out from Play Games and Firebase")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log.error("Failed to sign out from Play Games", e)
            }
        }
    }
