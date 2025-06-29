package com.yogeshpaliyal.chainreaction.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.chainreaction.game.GameState
import com.yogeshpaliyal.chainreaction.game.Player
import com.yogeshpaliyal.chainreaction.game.cellCapacity
import com.yogeshpaliyal.chainreaction.game.createInitialGrid
import com.yogeshpaliyal.chainreaction.game.getCellType
import com.yogeshpaliyal.chainreaction.game.placeMolecule
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChainReactionGame(
    gridWidth: Int,
    gridHeight: Int,
    players: List<Player>,
    enable3D: Boolean,
    onGameEnd: () -> Unit,
    onBackPressed: () -> Unit,
    showExitConfirmation: Boolean,
    onExitConfirmed: () -> Unit,
    onExitCancelled: () -> Unit
) {
    var gameState by remember {
        mutableStateOf(
            GameState(
                grid = createInitialGrid(gridWidth, gridHeight),
                players = players,
                currentPlayerIndex = 0,
                playerTurns = players.associate { it.id to 0 }
            )
        )
    }

    val density = LocalDensity.current
    var containerWidthPx by remember { mutableStateOf(0) }
    var containerHeightPx by remember { mutableStateOf(0) }

    // Handle back key press
    DisposableEffect(Unit) {
        // This is a placeholder for platform-specific back press handling
        // On Android, you would typically use BackHandler from accompanist
        onDispose { }
    }

    // Exit Game Confirmation Dialog
    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = onExitCancelled,
            title = { Text("Exit Game") },
            text = { Text("Are you sure you want to exit the current game? All progress will be lost.") },
            confirmButton = {
                TextButton(onClick = onExitConfirmed) {
                    Text("Yes, Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = onExitCancelled) {
                    Text("No, Continue")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reaction Cascade") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = TablerIcons.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(4.dp)
                .onSizeChanged {
                    containerWidthPx = it.width
                    containerHeightPx = it.height
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Game header with player info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (gameState.isGameOver) {
                    Text(
                        "Winner: ${gameState.winner?.name}",
                        color = gameState.winner?.color ?: Color.Black,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = onGameEnd) { Text("Play Again") }
                } else {
                    Text(
                        "Current Turn: ${gameState.players[gameState.currentPlayerIndex].name}",
                        color = gameState.players[gameState.currentPlayerIndex].color,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (!gameState.isGameOver) {
                // Calculate cell size based on available space
                val cellSizeDp = with(density) {
                    val cellWidthDp = (containerWidthPx / gridWidth).toDp()
                    val cellHeightDp = (containerHeightPx * 0.85f / gridHeight).toDp()
                    minOf(cellWidthDp, cellHeightDp)
                }

                // Game grid that adapts to screen size
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
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
                                    val cellType = getCellType(x, y, gridWidth, gridHeight)
                                    val borderColor = cell.owner?.color ?: Color.Gray

                                    // Cell with adaptive size
                                    Box(
                                        modifier = Modifier
                                            .size(cellSizeDp)
                                            .border(
                                                width = (cellSizeDp * 0.05f).coerceAtMost(3.dp),
                                                color = borderColor
                                            )
                                            .background(MaterialTheme.colorScheme.surface)
                                            .clickable(
                                                enabled = !gameState.isGameOver &&
                                                        (cell.owner == null || cell.owner == gameState.players[gameState.currentPlayerIndex])
                                            ) {
                                                gameState = placeMolecule(gameState, x, y)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (cell.molecules > 0) {
                                            // Scale molecule size based on cell size
                                            val moleculeSize = cellSizeDp * 0.8f
                                            AtomsMoleculesView(
                                                count = cell.molecules,
                                                color = cell.owner?.color ?: Color.Black,
                                                enable3D = enable3D,
                                                isCapturing = cell.captureAnimation,
                                                previousColor = cell.previousOwner?.color,
                                                size = moleculeSize
                                            )
                                        } else {
                                            Text(
                                                "${cellCapacity(cellType)}",
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
