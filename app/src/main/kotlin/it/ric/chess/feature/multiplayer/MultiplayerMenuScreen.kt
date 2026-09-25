package it.ric.chess.feature.multiplayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import it.ric.chess.R
import it.ric.chess.core.composable.BackButton
import it.ric.chess.core.composable.LoadingOverlayHost
import it.ric.chess.domain.model.Match
import it.ric.chess.navigation.Route

fun EntryProviderScope<Route>.multiplayerMenu() {
    entry<Route.MultiplayerMenu> {
        MultiplayerMenuScreen()
    }
}

@Composable
fun MultiplayerMenuScreen(viewModel: MultiplayerMenuViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MultiplayerMenuScreen(
        uiState = uiState,
        onBack = viewModel::onBack,
        onCreateMatch = viewModel::onCreateMatch,
        onJoinMatch = viewModel::onJoinMatch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiplayerMenuScreen(
    uiState: MultiplayerMenuUiState,
    onBack: () -> Unit,
    onCreateMatch: (String) -> Unit,
    onJoinMatch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var matchName by remember { mutableStateOf("") }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(stringResource(R.string.create_match)) },
            text = {
                OutlinedTextField(
                    value = matchName,
                    onValueChange = { matchName = it },
                    label = { Text(stringResource(R.string.match_name_label)) },
                    placeholder = { Text(stringResource(R.string.optional_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCreateMatch(matchName)
                        showCreateDialog = false
                        matchName = ""
                    },
                ) {
                    Text(stringResource(R.string.create))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.multi_player)) },
                navigationIcon = { BackButton(onBack) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        },
    ) { padding ->
        if (uiState.waitingMatches.isEmpty() && uiState.activeMatches.isEmpty()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(stringResource(R.string.no_matches), style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
            ) {
                if (uiState.activeMatches.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.my_active_matches),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                    items(uiState.activeMatches) { match ->
                        MatchItem(match, buttonText = stringResource(R.string.continue_button)) { onJoinMatch(match.id) }
                    }
                }

                if (uiState.waitingMatches.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.waiting_matches),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                    items(uiState.waitingMatches) { match ->
                        MatchItem(match, buttonText = stringResource(R.string.join_match)) { onJoinMatch(match.id) }
                    }
                }
            }
        }
    }

    LoadingOverlayHost(uiState.loadingState, Modifier.fillMaxSize())
}

@Composable
fun MatchItem(
    match: Match,
    buttonText: String,
    onJoin: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = match.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.status_format, match.status),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Button(onClick = onJoin) {
                Text(buttonText)
            }
        }
    }
}
