package it.ric.chess.core.viewmodel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LoadingStateExtTest :
    FunSpec({

        test("LoadingState whileLoading should manage loading state correctly") {
            val loadingState = LoadingState()
            loadingState.isCurrentlyLoading shouldBe false

            loadingState.whileLoading {
                loadingState.isCurrentlyLoading shouldBe true
            }

            loadingState.isCurrentlyLoading shouldBe false
        }
    })
