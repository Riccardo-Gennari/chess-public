package it.ric.chess.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.ric.chess.core.ui.model.UiText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationAlert(
    alert: UiConfirmationAlert,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = alert.negativeAction.onAction,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            if (alert.title != null) {
                Text(
                    text = alert.title.asString(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Text(text = alert.message.asString())

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilledTonalButton(
                    onClick = alert.negativeAction.onAction,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(alert.negativeAction.text.asString())
                }

                Button(
                    onClick = alert.positiveAction.onAction,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(alert.positiveAction.text.asString())
                }
            }
        }
    }
}

@Immutable
data class UiConfirmationAlert(
    val message: UiText,
    val positiveAction: UiAlertAction,
    val negativeAction: UiAlertAction,
    val title: UiText? = null,
)

@Immutable
data class UiAlertAction(
    val text: UiText,
    val onAction: () -> Unit,
)

@Preview
@Composable
private fun ConfirmationAlertPreview() {
    Box(Modifier.fillMaxSize()) {
        ConfirmationAlert(
            alert = UiConfirmationAlert(
                message = UiText.DynamicString("And this is the message"),
                title = UiText.DynamicString("This is a confirmation alert title"),
                positiveAction = UiAlertAction(UiText.Resource(android.R.string.ok)) {},
                negativeAction = UiAlertAction(UiText.Resource(android.R.string.cancel)) {},
            ),
        )
    }
}
