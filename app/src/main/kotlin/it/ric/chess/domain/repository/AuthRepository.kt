package it.ric.chess.domain.repository

import it.ric.chess.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val uid: String?
    val authUser: Flow<AuthUser?>

    suspend fun signInWithPlayGames(): Result<AuthUser?>

    suspend fun signOutFromPlayGames(): Result<Unit>
}
