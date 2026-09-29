package it.ric.chess.core.log

import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import io.mockk.verify

class LoggerTest :
    FunSpec({

        test("FilteredLogger should delegate when level is >= threshold") {
            val delegate = mockk<Logger>(relaxed = true)
            val filtered = FilteredLogger(LogLevel.WARNING, delegate)

            filtered.warning("Tag", "msg")
            verify { delegate.warning("Tag", "msg", null) }

            filtered.debug("Tag", "msg")
            verify(exactly = 0) { delegate.debug(any(), any(), any()) }
        }

        test("FixedTagLogger should call delegate with fixed tag") {
            val delegate = mockk<Logger>(relaxed = true)
            val fixed = FixedTagLogger("MyTag", delegate)

            fixed.info("msg")
            verify { delegate.info("MyTag", "msg", null) }
        }
    })
