package it.ric.chess.feature.match

import io.kotest.core.test.TestScope
import io.kotest.core.test.testCoroutineScheduler

fun TestScope.advanceUntilIdle() = testCoroutineScheduler.advanceUntilIdle()
