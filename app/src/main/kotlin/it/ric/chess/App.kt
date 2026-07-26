package it.ric.chess

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import it.ric.chess.core.composable.AppTheme
import it.ric.chess.navigation.Route
import it.ric.chess.navigation.strategy.SimpleOverlaySceneStrategy

@Composable
fun App(modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<AppViewModel>()
    val navigator = viewModel.navigator

    val sceneStrategies: List<SceneStrategy<Route>> =
        remember {
            listOf(SimpleOverlaySceneStrategy(), DialogSceneStrategy(), SinglePaneSceneStrategy())
        }

    AppTheme {
        NavDisplay(
            backStack = navigator.backStack,
            modifier = modifier,
            onBack = navigator::pop,
            sceneStrategies = sceneStrategies,
            entryDecorators =
                listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
            entryProvider =
                entryProvider {
                    viewModel.entryProviderInstallers.forEach { install ->
                        install()
                    }
                },
        )
    }
}
