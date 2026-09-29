package it.ric.chess.navigation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NavigatorTest :
    FunSpec({

        test("Navigator.noOp should implement methods safely") {
            val navigator = Navigator.noOp()
            navigator.currentBackStack shouldBe emptyList()
            navigator.pop() shouldBe null
            navigator.clearAndPush(Route.Menu())
            navigator.push(Route.Menu())
        }
    })
