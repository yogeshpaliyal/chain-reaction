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
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yogeshpaliyal.chainreaction.game.GameState
import com.yogeshpaliyal.chainreaction.game.Player
import com.yogeshpaliyal.chainreaction.game.cellCapacity
import com.yogeshpaliyal.chainreaction.game.createInitialGrid
import com.yogeshpaliyal.chainreaction.game.getCellType
import com.yogeshpaliyal.chainreaction.game.placeMolecule

@Composable
fun ChainReactionGame(
    gridWidth: Int,
    gridHeight: Int,
    players: List<Player>,
    enable3D: Boolean, // Toggle for 3D molecules
    onGameEnd: () -> Unit
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
    Column(
        modifier = Modifier.fillMaxSize().safeContentPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (gameState.isGameOver) {
            Text("Winner: ${gameState.winner?.name}", color = gameState.winner?.color ?: Color.Black)
            Button(onClick = onGameEnd) { Text("Play Again") }
        } else {
            Text("Current Turn: ${gameState.players[gameState.currentPlayerIndex].name}", color = gameState.players[gameState.currentPlayerIndex].color)
            Spacer(modifier = Modifier.size(16.dp))
            for (y in 0 until gridHeight) {
                Row {
                    for (x in 0 until gridWidth) {
                        val cell = gameState.grid[y][x]
                        val cellType = getCellType(x, y, gridWidth, gridHeight)
                        val borderColor = cell.owner?.color ?: Color.Gray
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .border(2.dp, borderColor)
                                .background(Color.White)
                                .clickable(enabled = !gameState.isGameOver && (cell.owner == null || cell.owner == gameState.players[gameState.currentPlayerIndex])) {
                                    gameState = placeMolecule(gameState, x, y)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (cell.molecules > 0) {
                                AtomsMoleculesView(
                                    count = cell.molecules,
                                    color = cell.owner?.color ?: Color.Black,
                                    enable3D = enable3D
                                )
                            } else {
                                Text("${cellCapacity(cellType)}", color = Color.LightGray, fontSize = MaterialTheme.typography.bodySmall.fontSize)
                            }
                        }
                    }
                }
            }
        }
    }
}

