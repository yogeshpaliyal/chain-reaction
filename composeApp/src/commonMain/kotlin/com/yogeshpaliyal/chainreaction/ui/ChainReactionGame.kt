package com.yogeshpaliyal.chainreaction.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.chainreaction.game.*
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowBack
import compose.icons.tablericons.Refresh
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChainReactionGame(
    gameConfig: GameConfig,
    onGameEnd: () -> Unit,
    onBackPressed: () -> Unit,
    showExitConfirmation: Boolean,
    onExitConfirmed: () -> Unit,
    onExitCancelled: () -> Unit
) {
    val viewModel = remember { GameViewModel(gameConfig) }
    val gameState by viewModel.gameState.collectAsState()
    val isAnimating by viewModel.isAnimating.collectAsState()

    val density = LocalDensity.current
    var containerWidthPx by remember { mutableStateOf(0) }
    var containerHeightPx by remember { mutableStateOf(0) }

    // Effect to handle game over state
    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver) {
            // Optional: Add a delay before calling onGameEnd to show the final state
            delay(2000)
            onGameEnd()
        }
    }

    // Exit Game Confirmation Dialog
    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = onExitCancelled,
            title = { Text("Exit Game") },
            text = { Text("Are you sure you want to exit the current game? All progress will be lost.") },
            confirmButton = { TextButton(onClick = onExitConfirmed) { Text("Yes, Exit") } },
            dismissButton = { TextButton(onClick = onExitCancelled) { Text("No, Continue") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reaction Cascade") },
                navigationIcon = { IconButton(onClick = onBackPressed) { Icon(TablerIcons.ArrowBack, "Back") } },
                actions = { IconButton(onClick = { viewModel.restartGame() }) { Icon(TablerIcons.Refresh, "Restart") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(4.dp).onSizeChanged {
                containerWidthPx = it.width
                containerHeightPx = it.height
            },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Game header with player info
            if (gameState.isGameOver) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Winner: ${gameState.winner?.name}",
                        color = gameState.winner?.color ?: Color.Black,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onGameEnd) { Text("Play Again") }
                }
            } else {
                Scoreboard(gameState)
            }

            if (!gameState.isGameOver) {
                val gridWidth = gameState.grid[0].size
                val gridHeight = gameState.grid.size
                val cellSizeDp = with(density) {
                    minOf((containerWidthPx / gridWidth).toDp(), (containerHeightPx * 0.85f / gridHeight).toDp())
                }

                // Game grid that adapts to screen size
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        for (y in 0 until gridHeight) {
                            Row {
                                for (x in 0 until gridWidth) {
                                    val cell = gameState.grid[y][x]
                                    val displayOwner = cell.owner ?: cell.previousOwner
                                    val borderColor = displayOwner?.color ?: gameState.players.getOrNull(gameState.currentPlayerIndex)?.color ?: Color.Gray

                                    // Cell with adaptive size
                                    Box(
                                        modifier = Modifier
                                            .graphicsLayer(clip = !cell.isExploding)
                                            .size(cellSizeDp)
                                            .border(
                                                width = (cellSizeDp * 0.02f).coerceAtMost(1.dp),
                                                color = borderColor
                                            )
                                            .background(MaterialTheme.colorScheme.surface)
                                            .clickable(
                                                enabled = !isAnimating && (cell.owner == null || cell.owner == gameState.players.getOrNull(gameState.currentPlayerIndex)),
                                                onClick = { viewModel.onCellClick(x, y) }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (cell.molecules > 0 || cell.isExploding) {
                                            // Scale molecule size based on cell size
                                            val moleculeSize = cellSizeDp * 0.8f
                                            AtomsMoleculesView(
                                                count = if (cell.isExploding) cell.explodingToPositions.size else cell.molecules,
                                                color = displayOwner?.color ?: Color.Black,
                                                enable3D = gameConfig.enable3D,
                                                isCapturing = cell.captureAnimation,
                                                previousColor = cell.previousOwner?.color,
                                                size = moleculeSize,
                                                explosionLevel = cell.explosionLevel,
                                                isExploding = cell.isExploding,
                                                explodingToPositions = cell.explodingToPositions,
                                                cellPosition = cell.x to cell.y
                                            )
                                        } else {
                                            Text(
                                                "${cellCapacity(getCellType(x, y, gridWidth, gridHeight))}",
                                                color = Color.LightGray,
                                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Scoreboard(gameState: GameState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Current Turn: ${gameState.players[gameState.currentPlayerIndex].name}",
            color = gameState.players[gameState.currentPlayerIndex].color,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            gameState.players.forEach { player ->
                val moleculeCount = gameState.grid.flatten().count { it.owner == player }
                val playerColor = animateColorAsState(
                    targetValue = if (player == gameState.players[gameState.currentPlayerIndex]) player.color else player.color.copy(alpha = 0.6f),
                    label = "playerColor"
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .border(
                            width = if (player == gameState.players[gameState.currentPlayerIndex]) 2.dp else 0.dp,
                            color = playerColor.value,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = player.name,
                        color = playerColor.value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$moleculeCount",
                        color = playerColor.value,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}
