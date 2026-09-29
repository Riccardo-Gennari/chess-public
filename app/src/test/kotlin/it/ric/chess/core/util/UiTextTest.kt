package it.ric.chess.core.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class UiTextTest :
    FunSpec({

        test("DynamicString asString should return value") {
            val uiText = UiText.DynamicString("Hello")
            uiText.value shouldBe "Hello"
        }
    })
