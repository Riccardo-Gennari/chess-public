package it.ric.chess.navigation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.ric.chess.feature.menu.navigation.MenuRoute

class NavigatorTest :
    FunSpec({

        test("Navigator.noOp should implement methods safely") {
            val navigator = Navigator.noOp()
            navigator.currentBackStack shouldBe emptyList()
            navigator.pop() shouldBe null
            navigator.clearAndPush(MenuRoute())
            navigator.push(MenuRoute())
        }
    })
