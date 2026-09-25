package it.ric.chess.domain.model

import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
data class PlayerInfo(
    val playerId: String,
    val displayName: String,
    val iconImageUri: Uri? = null,
) {
    companion object {
        val anonymous = PlayerInfo(playerId = "", displayName = "Player", iconImageUri = null)
    }
}
