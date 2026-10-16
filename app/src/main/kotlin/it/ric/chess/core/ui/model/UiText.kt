package it.ric.chess.core.ui.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    data class DynamicString(
        val value: String,
    ) : UiText()

    class Resource(
        @StringRes val resId: Int,
        vararg val args: Any,
    ) : UiText()

    @Composable
    @Suppress("SpreadOperator")
    fun asString(): String =
        when (this) {
            is DynamicString -> value
            is Resource -> stringResource(resId, *args)
        }
}
