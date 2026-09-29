package it.ric.chess.data.datasource.firebase.dto

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MatchDtoTest :
    FunSpec({

        test("MatchDto fields and constants should be correct") {
            val dto = MatchDto(
                id = "id",
                name = "name",
                whitePlayerId = "white",
                blackPlayerId = "black",
                whitePgsId = "wpgs",
                blackPgsId = "bps",
                fen = "fen",
                status = 0,
                timestamp = 100L,
            )

            dto.id shouldBe "id"
            MatchDto.FIELD_FEN shouldBe "f"
            MatchDto.FIELD_STATUS shouldBe "s"
            MatchDto.FIELD_TIMESTAMP shouldBe "t"
            MatchDto.FIELD_WHITE shouldBe "w"
            MatchDto.FIELD_BLACK shouldBe "b"
            MatchDto.FIELD_WHITE_PGS shouldBe "wp"
            MatchDto.FIELD_BLACK_PGS shouldBe "bp"
        }
    })
