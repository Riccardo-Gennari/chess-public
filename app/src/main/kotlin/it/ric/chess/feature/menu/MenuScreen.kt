package it.ric.chess.feature.menu

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import coil3.compose.AsyncImage
import it.ric.chess.R
import it.ric.chess.core.composable.AppTheme
import it.ric.chess.core.composable.LoadingOverlayHost
import it.ric.chess.core.model.PlayerInfo
import it.ric.chess.navigation.Route

fun EntryProviderScope<Route>.menu() {
    entry<Route.Menu> {
        MenuScreen()
    }
}

@Composable
fun MenuScreen(viewModel: MenuViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MenuScreen(
        uiState = uiState,
        onSinglePlayerSelect = viewModel::onSelectSinglePlayer,
        onMultiPlayerSelect = viewModel::onSelectMultiPlayer,
        onSignIn = viewModel::onSignIn,
        onSignOut = viewModel::onSignOut,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    uiState: MenuUiState,
    onSinglePlayerSelect: () -> Unit,
    onMultiPlayerSelect: () -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.chessboard),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).padding(end = 8.dp),
                        )
                        Text(stringResource(R.string.app_name))
                    }
                },
            )
        },
        modifier = modifier,
    ) { scaffoldPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
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
                    if (player != null) {
                        ListItem(
                            headlineContent = { Text(text = player.displayName) },
                            leadingContent = {
                                AsyncImage(
                                    model = player.iconImageUri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(R.drawable.person),
                                    modifier =
                                        Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
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
                    } else {
                        ListItem(
                            headlineContent = {},
                            leadingContent = {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.fillMaxHeight(0.25f))

            Card {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp),
                ) {
                    Button(
                        onClick = onSinglePlayerSelect,
                    ) {
                        Text(stringResource(R.string.local_multi_player))
                    }

                    Button(
                        onClick = onMultiPlayerSelect,
                    ) {
                        Text(stringResource(R.string.multi_player))
                    }
                }
            }
        }
    }

    LoadingOverlayHost(uiState.loadingState, Modifier.fillMaxSize())
}

@PreviewLightDark
@Composable
private fun MenuScreenPreview() {
    AppTheme {
        MenuScreen(
            uiState =
                MenuUiState(
                    playerInfo = PlayerInfo(playerId = "123", displayName = "John Doe"),
                ),
            onSinglePlayerSelect = {},
            onMultiPlayerSelect = {},
            onSignIn = {},
            onSignOut = {},
        )
    }
}
