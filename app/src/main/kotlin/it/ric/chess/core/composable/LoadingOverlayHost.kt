package it.ric.chess.core.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.ric.chess.core.viewmodel.LoadingState
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LoadingOverlayHost(
    loadingState: LoadingState,
    modifier: Modifier = Modifier,
    showDelay: Duration = 175.milliseconds,
    hideGracePeriod: Duration = 5.milliseconds,
) {
    val isLoading by loadingState.isLoading.collectAsStateWithLifecycle()
    LoadingOverlayHost(
        isLoading = isLoading,
        modifier = modifier,
        showDelay = showDelay,
        hideGracePeriod = hideGracePeriod,
    )
}

@Composable
fun LoadingOverlayHost(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    showDelay: Duration = 175.milliseconds,
    hideGracePeriod: Duration = 5.milliseconds,
) {
    if (LocalInspectionMode.current) {
        if (isLoading) LoadingOverlay(modifier)
        return
    }

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(showDelay)
            isVisible = true
        } else {
            delay(hideGracePeriod)
            isVisible = false
        }
    }

    if (isVisible) {
        LoadingOverlay(modifier)
    }
}

@Preview
@Composable
private fun LoadingOverlayHostPreview() {
    AppTheme {
        LoadingOverlayHost(isLoading = true)
    }
}
