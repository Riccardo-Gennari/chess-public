package it.ric.chess

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import it.ric.chess.navigation.nav3.EntryProviderInstaller
import it.ric.chess.navigation.nav3.Nav3Navigator

class AppViewModelTest :
    FunSpec({

        test("should hold navigator and installers") {
            val navigator = mockk<Nav3Navigator>()
            val installers = emptySet<EntryProviderInstaller>()

            val viewModel = AppViewModel(navigator, installers)

            viewModel.navigator shouldBe navigator
            viewModel.entryProviderInstallers shouldBe installers
        }
    })
