package it.ric.chess.feature.menu.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import coil3.compose.AsyncImage
import it.ric.chess.R
import it.ric.chess.core.ui.component.AppTheme
import it.ric.chess.core.ui.component.ConfirmationAlert
import it.ric.chess.core.ui.component.LoadingOverlayHost
import it.ric.chess.core.ui.component.UserMessageHost
import it.ric.chess.domain.model.PlayerInfo
import it.ric.chess.feature.menu.main.model.MainMenuUiState
import it.ric.chess.feature.menu.main.navigation.MainMenu
import it.ric.chess.navigation.Route

fun EntryProviderScope<Route>.mainMenu() {
    entry<MainMenu> {
        MainMenuScreen()
    }
}

@Composable
fun MainMenuScreen(viewModel: MainMenuViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MainMenuScreen(
        uiState = uiState,
        onDismissMessage = viewModel::onDismissMessage,
        onSinglePlayerSelect = viewModel::onSelectSinglePlayer,
        onMultiPlayerSelect = viewModel::onSelectMultiPlayer,
        onSignIn = viewModel::onSignIn,
        onSignOut = viewModel::onSignOut,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    uiState: MainMenuUiState,
    onDismissMessage: () -> Unit,
    onSinglePlayerSelect: () -> Unit,
    onMultiPlayerSelect: () -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.chessboard),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White, CircleShape)
                                    .padding(8.dp)
                                    .border(1.dp, Color.Black),
                            )
                            Text(stringResource(R.string.app_name))
                        }
                    },
                )
            },
            snackbarHost = {
                UserMessageHost(uiState.snackbarMessage, onDismissMessage)
            },
            modifier = Modifier.fillMaxSize(),
        ) { scaffoldPadding ->
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                        .padding(horizontal = 16.dp),
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                ) {
                    AnimatedContent(uiState.playerInfo) { player ->
                        ListItem(
                            headlineContent = { Text(text = player?.displayName.orEmpty()) },
                            overlineContent = {
                                if (player != null) Text("Logged in as:") else Text("You are not logged in")
                            },
                            leadingContent = {
                                var isError by remember { mutableStateOf(false) }
                                AsyncImage(
                                    model = player?.iconImageUri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    onError = { isError = true },
                                    onSuccess = { isError = false },
                                    onLoading = { isError = false },
                                    error = painterResource(R.drawable.person),
                                    modifier =
                                        Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                                            .then(if (isError) Modifier.padding(8.dp) else Modifier),
                                )
                            },
                            trailingContent = {
                                if (uiState.isSignedIn) {
                                    FilledIconButton(onClick = onSignOut) {
                                        Icon(
                                            painter = painterResource(R.drawable.logout),
                                            contentDescription = null,
                                        )
                                    }
                                } else {
                                    FilledIconButton(onClick = onSignIn) {
                                        Icon(
                                            painter = painterResource(R.drawable.login),
                                            contentDescription = null,
                                        )
                                    }
                                }
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        )
                    }
                }

                Card {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Button(
                            onClick = onSinglePlayerSelect,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.local_multi_player))
                        }

                        Button(
                            onClick = onMultiPlayerSelect,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.multi_player))
                        }
                    }
                }
            }
        }

        if (uiState.signInConfirmationAlert != null) {
            ConfirmationAlert(uiState.signInConfirmationAlert)
        }

        LoadingOverlayHost(uiState.loadingState, Modifier.fillMaxSize())
    }
}

@PreviewLightDark
@Composable
private fun MenuScreenPreview() {
    AppTheme {
        MainMenuScreen(
            uiState =
                MainMenuUiState(
                    playerInfo = PlayerInfo(playerId = "123", displayName = "John Doe"),
                ),
            onDismissMessage = {},
            onSinglePlayerSelect = {},
            onMultiPlayerSelect = {},
            onSignIn = {},
            onSignOut = {},
        )
    }
}

@Preview
@Composable
private fun MenuScreenLoggedOutPreview() {
    AppTheme {
        MainMenuScreen(
            uiState = MainMenuUiState(playerInfo = null),
            onDismissMessage = {},
            onSinglePlayerSelect = {},
            onMultiPlayerSelect = {},
            onSignIn = {},
            onSignOut = {},
        )
    }
}
