package it.ric.chess.feature.match.composable

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import it.ric.chess.R
import it.ric.chess.domain.model.MatchStatus

@Composable
fun GameOverDialog(
    gameStatus: MatchStatus,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
) {
    val message =
        remember(gameStatus) {
            when (gameStatus) {
                MatchStatus.WHITE_WINS -> R.string.white_wins
                MatchStatus.BLACK_WINS -> R.string.black_wins
                MatchStatus.DRAW -> R.string.draw
                MatchStatus.ONGOING -> -1
            }
        }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = {},
        title = { Text(stringResource(R.string.game_over)) },
        text = { if (message != -1) Text(stringResource(message)) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.play_again))
            }
        },
    )
}
