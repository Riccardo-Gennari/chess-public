package it.ric.chess.feature.match.logic

import it.ric.chess.domain.logic.toBoard as domainToBoard
import it.ric.chess.domain.logic.toFen as domainToFen
import it.ric.chess.domain.model.Board
import it.ric.chess.domain.model.PieceColor

fun Board.toFen(turn: PieceColor = PieceColor.WHITE): String = domainToFen(turn)
fun String.toBoard(): Pair<Board, PieceColor> = domainToBoard()
