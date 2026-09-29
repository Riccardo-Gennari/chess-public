package it.ric.chess.domain.usecase.match

import it.ric.chess.domain.model.GameMode
import it.ric.chess.domain.model.PieceColor
import javax.inject.Inject

class ResolvePlayerNameUseCase
    @Inject
    constructor() {
        operator fun invoke(
            assignedName: String?,
            playerColor: PieceColor,
            myColor: PieceColor?,
            gameMode: GameMode,
            myDisplayName: String?,
        ): String? {
            if (assignedName != null) return assignedName
            if (myColor == playerColor || gameMode == GameMode.LOCAL) return myDisplayName
            return null
        }
    }
