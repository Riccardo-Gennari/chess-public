package it.ric.chess.data.repository

import com.google.firebase.auth.PlayGamesAuthProvider
import it.ric.chess.core.log.Logger
import it.ric.chess.core.log.tag
import it.ric.chess.core.log.withFixedTag
import it.ric.chess.datasource.datastore.DataStore
import it.ric.chess.datasource.datastore.booleanDataKey
import it.ric.chess.datasource.datastore.persistentData
import it.ric.chess.datasource.firebase.Firebase
import it.ric.chess.datasource.playgames.PlayGamesDataSource
import it.ric.chess.domain.model.AuthUser
import it.ric.chess.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class FirebaseAuthRepository
    @Inject
    constructor(
        dataStore: DataStore,
        private val firebase: Firebase,
        private val playGamesDataSource: PlayGamesDataSource,
        log: Logger,
        @Named("webClientId") private val webClientId: String,
    ) : AuthRepository {
        override val uid: String? get() = firebase.uid
        override val authUser: Flow<AuthUser?> = firebase.observeAuthState()
        private val log = log.withFixedTag(tag)

        private val _isPlayGamesAuthAvailable = dataStore.persistentData(skipAuthKey)
        override val isPlayGamesAuthAvailable = _isPlayGamesAuthAvailable.map { it ?: true }

        override suspend fun signInWithPlayGames() {
            try {
                val serverAuthCode = playGamesDataSource.getAuthCode(webClientId)
                if (serverAuthCode != null) {
                    val credential = PlayGamesAuthProvider.getCredential(serverAuthCode)
                    firebase.signInWithCredential(credential)
                    log.debug("Signed in with Play Games: $uid")
                    _isPlayGamesAuthAvailable.set(false)
                } else {
                    _isPlayGamesAuthAvailable.set(true)
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
                _isPlayGamesAuthAvailable.set(false)
                firebase.signOut()
                log.debug("Signed out from Play Games and Firebase")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log.error("Failed to sign out from Play Games", e)
            }
        }

        companion object {
            val skipAuthKey = booleanDataKey("skipPlayGamesAuth")
        }
    }
