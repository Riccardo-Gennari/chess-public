package it.ric.chess.core.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

fun Color.brighten(factor: Float = 0.3f): Color =
    copy(
        red = red + (1f - red) * factor,
        green = green + (1f - green) * factor,
        blue = blue + (1f - blue) * factor,
        alpha = alpha,
    )

fun Color.darken(factor: Float = 0.3f): Color =
    copy(
        red = red * (1f - factor),
        green = green * (1f - factor),
        blue = blue * (1f - factor),
        alpha = alpha,
    )

fun Color.brightenByHue(amount: Float = 0.1f): Color {
    val outHsl = FloatArray(3)
    // 1. Convert Compose Color to Android HSL
    ColorUtils.colorToHSL(this.toArgb(), outHsl)

    // 2. Increase Lightness (index 2), keeping it capped at 1.0f
    outHsl[2] = (outHsl[2] + amount).coerceAtMost(1.0f)

    // 3. Convert back to Color
    val colorInt = ColorUtils.HSLToColor(outHsl)
    return Color(colorInt)
}

fun Color.darkenByHue(amount: Float = 0.1f): Color {
    val outHsl = FloatArray(3)
    // 1. Convert Compose Color to Android HSL
    ColorUtils.colorToHSL(this.toArgb(), outHsl)

    // 2. Decrease Lightness (index 2), keeping it floored at 0.0f
    outHsl[2] = (outHsl[2] - amount).coerceAtLeast(0.0f)

    // 3. Convert back to Color
    val colorInt = ColorUtils.HSLToColor(outHsl)
    return Color(colorInt)
}

fun Color.blend(
    color: Color,
    factor: Float,
): Color {
    val blended = ColorUtils.blendARGB(this.toArgb(), color.toArgb(), factor)
    return Color(blended)
}

/**
 * Multiplies each RGBA component of this color by another color,
 * matching the behavior of BlendMode.Modulate.
 */
fun Color.modulate(
    other: Color,
    preserveAlpha: Boolean = false,
): Color =
    Color(
        red = this.red * other.red,
        green = this.green * other.green,
        blue = this.blue * other.blue,
        alpha = if (preserveAlpha) this.alpha else this.alpha * other.alpha,
    )
