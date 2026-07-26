package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.AssertionMode
import io.kotest.engine.concurrency.SpecExecutionMode
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.engine.coroutines.ThreadPerSpecCoroutineContextFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectConfig : AbstractProjectConfig() {
    override val assertionMode = AssertionMode.None
    override val coroutineTestScope = true
    override val coroutineDispatcherFactory = ThreadPerSpecCoroutineContextFactory
    override val duplicateTestNameMode = DuplicateTestNameMode.Warn
    override val globalAssertSoftly = false
    override val isolationMode = IsolationMode.SingleInstance
    override val specExecutionMode = SpecExecutionMode.Sequential
    override val testExecutionMode = TestExecutionMode.Concurrent

    private val globalTestDispatcher = StandardTestDispatcher()

    override suspend fun beforeProject() {
        Dispatchers.setMain(globalTestDispatcher)
    }

    override suspend fun afterProject() {
        Dispatchers.resetMain()
    }
}
