package it.ric.chess.feature.match.component

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import it.ric.chess.R
import it.ric.chess.core.ui.component.AppTheme
import it.ric.chess.core.util.PreviewDynamicColors
import it.ric.chess.core.util.brighten
import it.ric.chess.core.util.brightenByHue
import it.ric.chess.domain.model.Piece
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.model.PieceType

@Composable
fun Piece(
    piece: Piece,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val colorFilter = remember(piece.color, colors) {
        val color = if (piece.color == PieceColor.WHITE) {
            colors.onPrimaryFixedVariant.brighten(0.9f)
        } else {
            colors.onPrimaryFixedVariant.brightenByHue(0.3f)
        }
        ColorFilter.tint(color, blendMode = BlendMode.Modulate)
    }
    val resource =
        remember(piece.type) {
            when (piece.type) {
                PieceType.KING -> R.drawable.king
                PieceType.QUEEN -> R.drawable.queen
                PieceType.ROOK -> R.drawable.rook
                PieceType.BISHOP -> R.drawable.bishop
                PieceType.KNIGHT -> R.drawable.knight
                PieceType.PAWN -> R.drawable.pawn
            }
        }

    Image(
        painter = painterResource(resource),
        contentDescription = null,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@PreviewDynamicColors
@Composable
private fun PieceBlackPreview() {
    AppTheme {
        Piece(Piece(PieceType.KING, PieceColor.BLACK))
    }
}

@PreviewDynamicColors
@Composable
private fun PieceWhitePreview() {
    AppTheme {
        Piece(Piece(PieceType.KING, PieceColor.WHITE))
    }
}
