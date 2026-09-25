package it.ric.chess.domain.repository

import it.ric.chess.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val uid: String?
    val isPlayGamesAuthAvailable: Flow<Boolean>
    val authUser: Flow<AuthUser?>

    suspend fun signInWithPlayGames()

    suspend fun signOutFromPlayGames()
}
