package it.ric.chess.data.repository

import com.google.firebase.auth.PlayGamesAuthProvider
import it.ric.chess.core.log.Logger
import it.ric.chess.core.log.tag
import it.ric.chess.core.log.withFixedTag
import it.ric.chess.core.util.runCatchingCancelable
import it.ric.chess.data.datasource.firebase.Firebase
import it.ric.chess.data.datasource.playgames.PlayGamesDataSource
import it.ric.chess.domain.model.AuthUser
import it.ric.chess.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named

class FirebaseAuthRepository
    @Inject
    constructor(
        private val firebase: Firebase,
        private val playGamesDataSource: PlayGamesDataSource,
        log: Logger,
        @Named("webClientId") private val webClientId: String,
    ) : AuthRepository {
        override val uid: String? get() = firebase.uid
        override val authUser: Flow<AuthUser?> = firebase.observeAuthState()
        private val log = log.withFixedTag(tag)

        override suspend fun signInWithPlayGames() =
            playGamesDataSource
                .getAuthCode(webClientId)
                .map { serverAuthCode ->
                    if (serverAuthCode != null) {
                        val credential = PlayGamesAuthProvider.getCredential(serverAuthCode)
                        firebase
                            .signInWithCredential(credential)
                            ?.uid
                            .also { log.debug("Signed in with Play Games: $it") }
                            ?.let { AuthUser(it) }
                    } else {
                        null
                    }
                }.onFailure {
                    log.debug("Failed to sign in with Play Games", it)
                }

        override suspend fun signOutFromPlayGames() =
            runCatchingCancelable {
                playGamesDataSource.signOut()
                firebase.signOut()
                log.debug("Signed out from Play Games and Firebase")
            }.onFailure { t ->
                log.error("Failed to sign out from Play Games", t)
            }
    }
