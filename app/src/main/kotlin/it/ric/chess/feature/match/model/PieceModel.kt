package it.ric.chess.feature.match.model

import it.ric.chess.domain.model.Board
import it.ric.chess.domain.model.Piece
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.model.PieceType
import it.ric.chess.domain.model.copy as domainCopy
import it.ric.chess.domain.model.emptyBoard as domainEmptyBoard
import it.ric.chess.domain.model.initialBoard as domainInitialBoard

typealias PieceType = PieceType
typealias PieceColor = PieceColor
typealias Piece = Piece
typealias Board = Board

fun Board.copy(): Board = domainCopy()

fun emptyBoard(): Board = domainEmptyBoard()

fun initialBoard(): Board = domainInitialBoard()
