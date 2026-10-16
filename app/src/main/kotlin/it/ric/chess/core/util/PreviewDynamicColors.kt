package it.ric.chess.core.util

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers.BLUE_DOMINATED_EXAMPLE
import androidx.compose.ui.tooling.preview.Wallpapers.GREEN_DOMINATED_EXAMPLE
import androidx.compose.ui.tooling.preview.Wallpapers.RED_DOMINATED_EXAMPLE
import androidx.compose.ui.tooling.preview.Wallpapers.YELLOW_DOMINATED_EXAMPLE

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "Red", wallpaper = RED_DOMINATED_EXAMPLE, apiLevel = 33)
@Preview(name = "Blue", wallpaper = BLUE_DOMINATED_EXAMPLE, apiLevel = 33)
@Preview(name = "Green", wallpaper = GREEN_DOMINATED_EXAMPLE, apiLevel = 33)
@Preview(name = "Yellow", wallpaper = YELLOW_DOMINATED_EXAMPLE, apiLevel = 33)
annotation class PreviewDynamicColors
