package it.ric.chess.domain.handler

import it.ric.chess.domain.logic.toBoard
import it.ric.chess.domain.logic.toFen
import it.ric.chess.domain.model.Board
import it.ric.chess.domain.model.GameMode
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.model.initialBoard
import it.ric.chess.domain.repository.MatchRepository
import it.ric.chess.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.transform

class RemoteMatchHandler(
    private val matchRepository: MatchRepository,
    private val playerRepository: PlayerRepository,
    private val matchId: String,
    private val currentUid: String?,
) : MatchHandler {
    override val gameMode: GameMode = GameMode.REMOTE

    private val nameCache = mutableMapOf<String, String>()

    override fun observeState(): Flow<MatchStateUpdate> =
        matchRepository
            .observeMatch(matchId)
            .distinctUntilChanged()
            .transform { match ->
                if (match != null) {
                    val (newBoard, newTurn) = match.fen.toBoard()

                    val playerColor =
                        when {
                            match.whitePlayerId == currentUid -> PieceColor.WHITE
                            match.blackPlayerId == currentUid -> PieceColor.BLACK
                            else -> null
                        }

                    // Emit immediate state without names (or with cached names)
                    var whiteName = match.whitePgsId?.let { nameCache[it] }
                    var blackName = match.blackPgsId?.let { nameCache[it] }

                    emit(
                        MatchStateUpdate(
                            board = newBoard,
                            turn = newTurn,
                            status = match.status,
                            playerColor = playerColor,
                            whitePlayerName = whiteName,
                            blackPlayerName = blackName,
                        ),
                    )

                    // Fetch missing names and re-emit if updated
                    var updated = false
                    if (whiteName == null && match.whitePgsId != null) {
                        whiteName = getOrFetchName(match.whitePgsId)
                        updated = true
                    }
                    if (blackName == null && match.blackPgsId != null) {
                        blackName = getOrFetchName(match.blackPgsId)
                        updated = true
                    }

                    if (updated) {
                        emit(
                            MatchStateUpdate(
                                board = newBoard,
                                turn = newTurn,
                                status = match.status,
                                playerColor = playerColor,
                                whitePlayerName = whiteName,
                                blackPlayerName = blackName,
                            ),
                        )
                    }
                } else {
                    emit(
                        MatchStateUpdate(
                            board = initialBoard(),
                            turn = PieceColor.WHITE,
                            status = MatchStatus.ONGOING,
                        ),
                    )
                }
            }

    private suspend fun getOrFetchName(pgsId: String): String? {
        nameCache[pgsId]?.let { return it }
        val info = playerRepository.getPlayerInfo(pgsId)
        return info?.displayName?.also { nameCache[pgsId] = it }
    }

    override suspend fun onMove(
        nextBoard: Board,
        nextTurn: PieceColor,
        nextStatus: MatchStatus,
    ) {
        matchRepository.updateMove(matchId, nextBoard.toFen(nextTurn), nextStatus)
    }

    override suspend fun onReset() {
        // Not supported for remote yet
    }

    override suspend fun onQuit() {
        currentUid?.let { uid ->
            matchRepository.quitMatch(matchId, uid)
        }
    }
}
