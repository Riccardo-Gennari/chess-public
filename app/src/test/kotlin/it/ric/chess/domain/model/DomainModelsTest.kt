package it.ric.chess.domain.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class DomainModelsTest :
    FunSpec({

        test("Match data class properties should be correct") {
            val match = Match(
                id = "m1",
                name = "Test Match",
                whitePlayerId = "w1",
                blackPlayerId = "b1",
                whitePgsId = "wp1",
                blackPgsId = "bp1",
                fen = "fen",
                status = MatchStatus.ONGOING,
                lastUpdate = 12345L,
            )
            match.id shouldBe "m1"
            match.name shouldBe "Test Match"
            match.status shouldBe MatchStatus.ONGOING
        }

        test("Piece data class properties should be correct") {
            val piece = Piece(PieceType.ROOK, PieceColor.WHITE, hasMoved = true)
            piece.type shouldBe PieceType.ROOK
            piece.color shouldBe PieceColor.WHITE
            piece.hasMoved shouldBe true
        }

        test("PlayerInfo anonymous should be correct") {
            val anon = PlayerInfo.anonymous
            anon.playerId shouldBe ""
            anon.displayName shouldBe "Player"
        }

        test("GameMode and MatchStatus values should exist") {
            GameMode.entries.size shouldNotBe 0
            MatchStatus.entries.size shouldNotBe 0
        }

        test("AuthUser should hold uid") {
            val user = AuthUser("uid-123")
            user.uid shouldBe "uid-123"
        }
    })
