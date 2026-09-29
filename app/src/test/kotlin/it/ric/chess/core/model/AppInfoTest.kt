package it.ric.chess.core.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AppInfoTest :
    FunSpec({

        test("AppInfo data class should hold appName") {
            val appInfo = AppInfo("ChessApp")
            appInfo.appName shouldBe "ChessApp"
        }
    })
