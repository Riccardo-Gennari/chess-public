package it.ric.chess.feature.match.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import it.ric.chess.core.composable.AppTheme
import it.ric.chess.domain.model.Board
import it.ric.chess.domain.model.Piece
import it.ric.chess.domain.model.emptyBoard
import it.ric.chess.domain.model.initialBoard

/**
 * Simple 8x8 chessboard with alternating light/dark cells.
 * Each cell exposes a composable slot (cellContent) for rendering chess pieces.
 * Cells are clickable and invoke onCellClick callback for interaction with ViewModel.
 */
@Composable
fun Chessboard(
    modifier: Modifier = Modifier,
    board: Board = emptyBoard(),
    selectedCell: Pair<Int, Int>? = null,
    validMoves: Set<Pair<Int, Int>> = emptySet(),
    onCellClick: (row: Int, col: Int) -> Unit = { _, _ -> },
    pieceContent: @Composable (row: Int, col: Int, piece: Piece?) -> Unit = { _, _, _ -> },
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier.aspectRatio(1f)) {
        for (row in 0 until 8) {
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                for (col in 0 until 8) {
                    val isLight = (row + col) % 2 == 0
                    val isSelected = selectedCell?.let { (r, c) -> r == row && c == col } ?: false
                    val isValidMove = validMoves.contains(Pair(row, col))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    if (isLight) colors.primaryContainer else colors.primary,
                                ).then(
                                    if (isSelected) {
                                        Modifier.background(colors.secondaryContainer.copy(alpha = 0.6f))
                                    } else if (isValidMove) {
                                        Modifier.background(colors.tertiaryContainer.copy(alpha = 0.6f))
                                    } else {
                                        Modifier
                                    },
                                ).clickable { onCellClick(row, col) },
                    ) {
                        pieceContent(row, col, board[row][col])
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ChessboardPreview() {
    AppTheme {
        Chessboard(
            modifier = Modifier.size(320.dp),
            board = initialBoard(),
            pieceContent = { _, _, piece ->
                if (piece != null) {
                    Piece(piece)
                }
            },
        )
    }
}
