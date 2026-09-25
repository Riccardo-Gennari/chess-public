package it.ric.chess.feature.match.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.ric.chess.R
import it.ric.chess.domain.model.PieceColor

@Composable
fun PlayerInfo(
    color: PieceColor,
    isCurrentTurn: Boolean,
    isMe: Boolean,
    modifier: Modifier = Modifier,
    playerName: String? = null,
) {
    val colors = MaterialTheme.colorScheme
    val typo = MaterialTheme.typography
    val label =
        when {
            isMe -> if (color == PieceColor.WHITE) stringResource(R.string.white_you) else stringResource(R.string.black_you)

            else -> if (color ==
                PieceColor.WHITE
            ) {
                stringResource(R.string.white_player)
            } else {
                stringResource(R.string.black_player)
            }
        }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = colors.surfaceVariant.copy(alpha = if (isCurrentTurn) 1f else 0.5f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TurnIndicator(isCurrentTurn = isCurrentTurn, color = color)

            Column {
                Text(
                    text = label,
                    style = typo.bodyLarge,
                    fontWeight = if (isCurrentTurn) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrentTurn) colors.onSurfaceVariant else colors.onSurfaceVariant.copy(alpha = 0.7f),
                )
                if (playerName != null) {
                    Text(
                        text = playerName,
                        style = typo.bodySmall,
                        color = colors.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                }
            }

            val enterTransition =
                remember {
                    fadeIn(tween(150)) +
                        expandHorizontally(expandFrom = Alignment.Start) +
                        slideInHorizontally { -it / 2 }
                }

            val exitTransition =
                remember {
                    fadeOut(tween(150)) +
                        shrinkHorizontally(shrinkTowards = Alignment.Start) +
                        slideOutHorizontally { -it / 2 }
                }

            AnimatedVisibility(
                visible = isCurrentTurn,
                enter = enterTransition,
                exit = exitTransition,
            ) {
                Surface(
                    color = colors.primary,
                    shape = CircleShape,
                ) {
                    Text(
                        text = stringResource(R.string.turn_indicator),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = typo.labelSmall,
                        color = colors.onPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun TurnIndicator(
    isCurrentTurn: Boolean,
    color: PieceColor,
    modifier: Modifier = Modifier,
) {
    val indicatorColor = if (color == PieceColor.WHITE) Color.White else Color.Black
    val borderColor = if (color == PieceColor.WHITE) Color.Black else Color.White

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (isCurrentTurn) {
            val infiniteTransition = rememberInfiniteTransition(label = "turn")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.4f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(1000),
                        repeatMode = RepeatMode.Reverse,
                    ),
                label = "scale",
            )
            Box(
                modifier =
                    Modifier
                        .size(16.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(indicatorColor.copy(alpha = 0.3f)),
            )
        }

        Box(
            modifier =
                Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(indicatorColor)
                    .background(borderColor.copy(alpha = 0.1f)), // Subtle border/inner shadow
        )
    }
}
