package it.ric.chess.domain.usecase.match

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.ric.chess.domain.model.GameMode
import it.ric.chess.domain.model.PieceColor

class ResolvePlayerNameUseCaseTest :
    FunSpec({
        val useCase = ResolvePlayerNameUseCase()

        test("should return assigned name if present") {
            val name = useCase(
                assignedName = "Assigned",
                playerColor = PieceColor.WHITE,
                myColor = PieceColor.BLACK,
                gameMode = GameMode.REMOTE,
                myDisplayName = "MyName",
            )
            name shouldBe "Assigned"
        }

        test("should return myDisplayName if my color matches player color") {
            val name = useCase(
                assignedName = null,
                playerColor = PieceColor.WHITE,
                myColor = PieceColor.WHITE,
                gameMode = GameMode.REMOTE,
                myDisplayName = "MyName",
            )
            name shouldBe "MyName"
        }

        test("should return myDisplayName in LOCAL game mode") {
            val name = useCase(
                assignedName = null,
                playerColor = PieceColor.BLACK,
                myColor = PieceColor.WHITE,
                gameMode = GameMode.LOCAL,
                myDisplayName = "LocalName",
            )
            name shouldBe "LocalName"
        }

        test("should return null otherwise") {
            val name = useCase(
                assignedName = null,
                playerColor = PieceColor.BLACK,
                myColor = PieceColor.WHITE,
                gameMode = GameMode.REMOTE,
                myDisplayName = "MyName",
            )
            name shouldBe null
        }
    })
