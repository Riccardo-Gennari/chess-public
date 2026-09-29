package it.ric.chess.data.datasource.firebase.mapper

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.ric.chess.data.datasource.firebase.dto.MatchDto
import it.ric.chess.domain.model.Match
import it.ric.chess.domain.model.MatchStatus

class MatchMapperTest :
    FunSpec({

        test("MatchDto toDomain should map correctly") {
            val dto = MatchDto(
                id = "m1",
                name = "Match 1",
                whitePlayerId = "w1",
                blackPlayerId = "b1",
                whitePgsId = "wp1",
                blackPgsId = "bp1",
                fen = "fen-string",
                status = MatchStatus.ONGOING.ordinal,
                timestamp = 1000L,
            )

            val domain = dto.toDomain()

            domain.id shouldBe "m1"
            domain.name shouldBe "Match 1"
            domain.whitePlayerId shouldBe "w1"
            domain.blackPlayerId shouldBe "b1"
            domain.whitePgsId shouldBe "wp1"
            domain.blackPgsId shouldBe "bp1"
            domain.fen shouldBe "fen-string"
            domain.status shouldBe MatchStatus.ONGOING
            domain.lastUpdate shouldBe 1000L
        }

        test("Match toDto should map correctly") {
            val domain = Match(
                id = "m1",
                name = "Match 1",
                whitePlayerId = "w1",
                blackPlayerId = "b1",
                whitePgsId = "wp1",
                blackPgsId = "bp1",
                fen = "fen-string",
                status = MatchStatus.WHITE_WINS,
                lastUpdate = 1000L,
            )

            val dto = domain.toDto()

            dto.id shouldBe "m1"
            dto.name shouldBe "Match 1"
            dto.whitePlayerId shouldBe "w1"
            dto.blackPlayerId shouldBe "b1"
            dto.whitePgsId shouldBe "wp1"
            dto.blackPgsId shouldBe "bp1"
            dto.fen shouldBe "fen-string"
            dto.status shouldBe MatchStatus.WHITE_WINS.ordinal
            dto.timestamp shouldBe 1000L
        }

        test("createMoveUpdate should return correct map") {
            val update = createMoveUpdate("new-fen", MatchStatus.BLACK_WINS)
            update[MatchDto.FIELD_FEN] shouldBe "new-fen"
            update[MatchDto.FIELD_STATUS] shouldBe MatchStatus.BLACK_WINS.ordinal
        }

        test("createJoinBlackUpdate should return correct map") {
            val update = createJoinBlackUpdate("b-user", "b-pgs")
            update[MatchDto.FIELD_BLACK] shouldBe "b-user"
            update[MatchDto.FIELD_BLACK_PGS] shouldBe "b-pgs"
        }
    })
