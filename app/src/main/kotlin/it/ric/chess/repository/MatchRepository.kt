package it.ric.chess.repository

import it.ric.chess.core.model.Match
import it.ric.chess.core.model.MatchStatus
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    /**
     * Creates a new match and sets the given user as the white player.
     * @param uid The ID of the authenticated user creating the match.
     * @param initialFen The starting FEN string.
     * @param name Optional name for the match.
     * @param whitePgsId Optional Play Games ID for the white player.
     * @return The ID of the created match.
     */
    suspend fun createMatch(
        uid: String,
        initialFen: String,
        name: String? = null,
        whitePgsId: String? = null,
    ): String

    /**
     * Joins an existing match as the black player if the slot is empty.
     * @param matchId The ID of the match to join.
     * @param uid The ID of the authenticated user joining the match.
     * @param blackPgsId Optional Play Games ID for the black player.
     * @return True if joined or already in the match, false if the match is full.
     */
    suspend fun joinMatch(
        matchId: String,
        uid: String,
        blackPgsId: String? = null,
    ): Boolean

    /**
     * Removes the given user from the match.
     * If no more players are in the match, it is deleted.
     */
    suspend fun quitMatch(matchId: String, uid: String)

    /**
     * Updates the board state (FEN) and status.
     */
    suspend fun updateMove(
        matchId: String,
        fen: String,
        status: MatchStatus = MatchStatus.ONGOING,
    )

    /**
     * Observes matches that are waiting for a second player.
     */
    fun observeWaitingMatches(): Flow<List<Match>>

    /**
     * Observes matches where the given user is a participant.
     */
    fun observeMyMatches(uid: String): Flow<List<Match>>

    /**
     * Observes a specific match for real-time updates.
     */
    fun observeMatch(matchId: String): Flow<Match?>

    /**
     * Observes the locally saved match state (FEN).
     */
    fun observeLocalMatch(): Flow<String?>

    /**
     * Persists the local match state (FEN).
     */
    suspend fun saveLocalMatch(fen: String)

    /**
     * Clears the locally saved match state.
     */
    suspend fun clearLocalMatch()
}
