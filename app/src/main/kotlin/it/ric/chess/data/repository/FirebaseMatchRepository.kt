package it.ric.chess.data.repository

import com.google.firebase.database.getValue
import it.ric.chess.datasource.datastore.DataStore
import it.ric.chess.datasource.datastore.stringDataKey
import it.ric.chess.datasource.firebase.Firebase
import it.ric.chess.datasource.firebase.dto.MatchDto
import it.ric.chess.datasource.firebase.mapper.createJoinBlackUpdate
import it.ric.chess.datasource.firebase.mapper.createMoveUpdate
import it.ric.chess.datasource.firebase.mapper.toDomain
import it.ric.chess.datasource.firebase.mapper.toDto
import it.ric.chess.domain.model.Match
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Firebase implementation of [MatchRepository].
 */
class FirebaseMatchRepository
    @Inject
    constructor(
        private val firebase: Firebase,
        private val dataStore: DataStore,
    ) : MatchRepository {
        override suspend fun createMatch(
            uid: String,
            initialFen: String,
            name: String?,
            whitePgsId: String?,
        ): String {
            val id = firebase.generateId(PATH_MATCHES)
            val match =
                Match(
                    id = id,
                    name = if (name.isNullOrBlank()) "Match ${id.takeLast(6)}" else name,
                    whitePlayerId = uid,
                    blackPlayerId = null,
                    whitePgsId = whitePgsId,
                    blackPgsId = null,
                    fen = initialFen,
                    status = MatchStatus.ONGOING,
                    lastUpdate = System.currentTimeMillis(),
                )
            firebase.setValue("$PATH_MATCHES/$id", match.toDto())
            return id
        }

        override suspend fun joinMatch(
            matchId: String,
            uid: String,
            blackPgsId: String?,
        ): Boolean {
            val dto = firebase.getValue("$PATH_MATCHES/$matchId", MatchDto::class.java) ?: return false

            // Join as black if the slot is empty
            if (dto.blackPlayerId == null && dto.whitePlayerId != uid) {
                firebase.updateChildren("$PATH_MATCHES/$matchId", createJoinBlackUpdate(uid, blackPgsId))
                return true
            }

            // Already in the match (either as white or black)
            return dto.blackPlayerId == uid || dto.whitePlayerId == uid
        }

        override suspend fun quitMatch(
            matchId: String,
            uid: String,
        ) {
            val dto = firebase.getValue("$PATH_MATCHES/$matchId", MatchDto::class.java) ?: return

            val isWhite = dto.whitePlayerId == uid
            val isBlack = dto.blackPlayerId == uid

            if (!isWhite && !isBlack) return

            val newWhite = if (isWhite) null else dto.whitePlayerId
            val newBlack = if (isBlack) null else dto.blackPlayerId

            if (newWhite == null && newBlack == null) {
                // No players left, delete the match
                firebase.setValue("$PATH_MATCHES/$matchId", null)
            } else {
                // Remove the quitting player
                val updates = mutableMapOf<String, Any?>()
                if (isWhite) updates[MatchDto.FIELD_WHITE] = null
                if (isBlack) updates[MatchDto.FIELD_BLACK] = null
                updates[MatchDto.FIELD_TIMESTAMP] = System.currentTimeMillis()
                firebase.updateChildren("$PATH_MATCHES/$matchId", updates)
            }
        }

        override suspend fun updateMove(
            matchId: String,
            fen: String,
            status: MatchStatus,
        ) {
            firebase.updateChildren("$PATH_MATCHES/$matchId", createMoveUpdate(fen, status))
        }

        override fun observeWaitingMatches(): Flow<List<Match>> =
            firebase
                .observeQuery { root ->
                    root.child(PATH_MATCHES).orderByChild(MatchDto.FIELD_BLACK).equalTo(null)
                }.map { snapshot ->
                    snapshot.children
                        .mapNotNull { it.getValue<MatchDto>()?.toDomain() }
                        .filter { it.status == MatchStatus.ONGOING }
                }

        override fun observeMyMatches(uid: String): Flow<List<Match>> {
            val whiteMatches =
                firebase
                    .observeQuery { root ->
                        root.child(PATH_MATCHES).orderByChild(MatchDto.FIELD_WHITE).equalTo(uid)
                    }.map { snapshot -> snapshot.children.mapNotNull { it.getValue<MatchDto>()?.toDomain() } }

            val blackMatches =
                firebase
                    .observeQuery { root ->
                        root.child(PATH_MATCHES).orderByChild(MatchDto.FIELD_BLACK).equalTo(uid)
                    }.map { snapshot -> snapshot.children.mapNotNull { it.getValue<MatchDto>()?.toDomain() } }

            return combine(whiteMatches, blackMatches) { white, black ->
                (white + black).distinctBy { it.id }.sortedByDescending { it.lastUpdate }
            }
        }

        override fun observeMatch(matchId: String): Flow<Match?> =
            firebase.observeValue("$PATH_MATCHES/$matchId").map { snapshot ->
                snapshot.getValue<MatchDto>()?.toDomain()
            }

        override fun observeLocalMatch(): Flow<String?> = dataStore[localMatchKey]

        override suspend fun saveLocalMatch(fen: String) {
            dataStore.put(localMatchKey, fen)
        }

        override suspend fun clearLocalMatch() {
            dataStore.remove(localMatchKey)
        }

        companion object {
            private const val PATH_MATCHES = "matches"
            private val localMatchKey = stringDataKey("local_match_fen")
        }
    }
