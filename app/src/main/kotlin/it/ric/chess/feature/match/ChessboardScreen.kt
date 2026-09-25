package it.ric.chess.feature.match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import it.ric.chess.R
import it.ric.chess.core.composable.AppTheme
import it.ric.chess.core.composable.BackButton
import it.ric.chess.core.composable.LoadingOverlayHost
import it.ric.chess.core.composable.UserMessageHost
import it.ric.chess.core.viewmodel.LoadingState
import it.ric.chess.domain.model.GameMode
import it.ric.chess.domain.model.MatchStatus
import it.ric.chess.domain.model.PieceColor
import it.ric.chess.domain.model.initialBoard
import it.ric.chess.feature.match.composable.Chessboard
import it.ric.chess.feature.match.composable.GameOverDialog
import it.ric.chess.feature.match.composable.Piece
import it.ric.chess.feature.match.composable.PlayerInfo
import it.ric.chess.feature.match.model.ChessUiState
import it.ric.chess.navigation.Route

fun EntryProviderScope<Route>.chessboard() {
    entry<Route.Chessboard> { route ->
        ChessboardScreen(matchId = route.matchId)
    }
}

@Composable
fun ChessboardScreen(
    matchId: String? = null,
    viewModel: ChessViewModel =
        hiltViewModel<ChessViewModel, ChessViewModel.Factory>(
            key = matchId,
            creationCallback = { factory -> factory.create(matchId) },
        ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChessboardScreen(
        uiState = uiState,
        onBack = viewModel::onBack,
        onQuit = viewModel::quitMatch,
        onCellClick = viewModel::onCellClicked,
        onReset = viewModel::reset,
        onUserMessageShown = viewModel::onUserMessageShown,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessboardScreen(
    uiState: ChessUiState,
    onBack: () -> Unit,
    onQuit: () -> Unit,
    onCellClick: (Int, Int) -> Unit,
    onReset: () -> Unit,
    onUserMessageShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val myColor = uiState.myColor ?: PieceColor.WHITE
    val opponentColor = if (myColor == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE

    Box(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.match_title)) },
                    navigationIcon = { BackButton(onBack) },
                    actions = {
                        if (uiState.gameMode == GameMode.LOCAL) {
                            TextButton(onClick = onReset) {
                                Text(stringResource(R.string.restart))
                            }
                        }
                        if (uiState.myColor != null) {
                            IconButton(onClick = onQuit) {
                                Icon(
                                    painter = painterResource(R.drawable.logout),
                                    contentDescription = stringResource(R.string.quit_match),
                                )
                            }
                        }
                    },
                )
            },
            snackbarHost = {
                UserMessageHost(
                    userMessage = uiState.userMessage,
                    onMessageShown = onUserMessageShown,
                )
            },
        ) { scaffoldPadding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                        .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            ) {
                PlayerInfo(
                    color = opponentColor,
                    isCurrentTurn = uiState.currentTurn == opponentColor,
                    isMe = false,
                    playerName = if (opponentColor == PieceColor.WHITE) uiState.whitePlayerName else uiState.blackPlayerName,
                )

                Surface(
                    modifier = Modifier,
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp,
                ) {
                    Chessboard(
                        selectedCell = uiState.selected,
                        validMoves = uiState.validMoves,
                        board = uiState.board,
                        onCellClick = onCellClick,
                    ) { _, _, piece ->
                        if (piece == null) return@Chessboard
                        Piece(piece)
                    }
                }

                PlayerInfo(
                    color = myColor,
                    isCurrentTurn = uiState.currentTurn == myColor,
                    isMe = uiState.myColor != null,
                    playerName = if (myColor == PieceColor.WHITE) uiState.whitePlayerName else uiState.blackPlayerName,
                )
            }

            // Show game over dialog
            if (uiState.gameStatus != MatchStatus.ONGOING) {
                GameOverDialog(
                    gameStatus = uiState.gameStatus,
                    onDismiss = if (uiState.gameMode == GameMode.LOCAL) onReset else onQuit,
                )
            }
        }

        LoadingOverlayHost(uiState.loadingState, Modifier.fillMaxSize())
    }
}

@PreviewLightDark
@Composable
private fun ChessboardScreenPreview() {
    AppTheme {
        ChessboardScreen(
            uiState = ChessUiState(board = initialBoard()),
            onBack = {},
            onQuit = {},
            onCellClick = { _, _ -> },
            onReset = {},
            onUserMessageShown = {},
        )
    }
}

@Preview
@Composable
private fun ChessboardScreenLoadingPreview() {
    AppTheme {
        ChessboardScreen(
            uiState = ChessUiState(board = initialBoard(), loadingState = LoadingState.Loading),
            onBack = {},
            onQuit = {},
            onCellClick = { _, _ -> },
            onReset = {},
            onUserMessageShown = {},
        )
    }
}
