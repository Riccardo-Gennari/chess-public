package it.ric.chess.domain.model

data class PlayerInfo(
    val playerId: String,
    val displayName: String,
    val iconImageUri: String? = null,
) {
    companion object {
        val anonymous = PlayerInfo(playerId = "", displayName = "Player", iconImageUri = null)
        val guest = PlayerInfo(playerId = "", displayName = "Guest", iconImageUri = null)
    }
}

val (PlayerInfo?).isSignedIn: Boolean get() = this != null && this.playerId.isNotBlank()
