package it.ric.chess.navigation.strategy

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.MetadataScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

class SimpleOverlaySceneStrategy<T : Any> : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.lastOrNull()
        lastEntry?.metadata?.get(IS_OVERLAY) ?: return null
        @Suppress("UNCHECKED_CAST")
        return SimpleOverlayScene(
            key = lastEntry.contentKey as T,
            previousEntries = entries.dropLast(1),
            overlaidEntries = entries.dropLast(1),
            entry = lastEntry,
        )
    }

    companion object {
        fun overlayMetadata(): Map<String, Any> =
            metadata {
                this.overlay()
            }

        fun MetadataScope.overlay() = put(IS_OVERLAY, true)

        internal val IS_OVERLAY = object : NavMetadataKey<Boolean> {}
    }
}

internal data class SimpleOverlayScene<T : Any>(
    override val key: T,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
) : OverlayScene<T> {
    override val entries: List<NavEntry<T>> = listOf(entry)
    override val content: @Composable (() -> Unit) = { entry.Content() }
}
