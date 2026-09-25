package it.ric.chess.feature.match.composable

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import it.ric.chess.R
import it.ric.chess.domain.model.Piece
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.model.PieceType

@Composable
fun Piece(
    piece: Piece,
    modifier: Modifier = Modifier,
) {
    val resource =
        remember(piece.color, piece.type) {
            when (piece.color) {
                PieceColor.WHITE -> {
                    when (piece.type) {
                        PieceType.KING -> R.drawable.king_light
                        PieceType.QUEEN -> R.drawable.queen_light
                        PieceType.ROOK -> R.drawable.rook_light
                        PieceType.BISHOP -> R.drawable.bishop_light
                        PieceType.KNIGHT -> R.drawable.knight_light
                        PieceType.PAWN -> R.drawable.pawn_light
                    }
                }

                PieceColor.BLACK -> {
                    when (piece.type) {
                        PieceType.KING -> R.drawable.king_dark
                        PieceType.QUEEN -> R.drawable.queen_dark
                        PieceType.ROOK -> R.drawable.rook_dark
                        PieceType.BISHOP -> R.drawable.bishop_dark
                        PieceType.KNIGHT -> R.drawable.knight_dark
                        PieceType.PAWN -> R.drawable.pawn_dark
                    }
                }
            }
        }

    Image(
        painter = painterResource(resource),
        contentDescription = null,
        modifier = modifier,
    )
}
