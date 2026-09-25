package it.ric.chess.core.composable

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import it.ric.chess.core.util.UiText

/**
 * A dedicated composable to handle showing snackbars from a user message string.
 * It encapsulates the [LaunchedEffect] and [SnackbarHostState] logic.
 */
@Composable
fun UserMessageHost(
    userMessage: UiText?,
    onMessageShown: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val messageText = userMessage?.asString()
    val currentOnMessageShown by rememberUpdatedState(onMessageShown)

    LaunchedEffect(messageText) {
        if (messageText != null) {
            snackbarHostState.showSnackbar(message = messageText)
            currentOnMessageShown()
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier,
    )
}
