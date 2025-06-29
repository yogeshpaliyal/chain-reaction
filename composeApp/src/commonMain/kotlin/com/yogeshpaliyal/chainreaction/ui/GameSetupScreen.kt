package com.yogeshpaliyal.chainreaction.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class GameConfig(
    val gridWidth: Int = 8,
    val gridHeight: Int = 16,
    val playerCount: Int = 2,
    val enable3D: Boolean = true,
    val playerNames: List<String> = listOf("Player 1", "Player 2"),
    val darkMode: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSetupScreen(
    onStartGame: (GameConfig) -> Unit,
    initialDarkMode: Boolean = false,
    onThemeChanged: (Boolean) -> Unit = {}
) {
    var config by remember { mutableStateOf(GameConfig(darkMode = initialDarkMode)) }

    // Adjust player names list when player count changes
    LaunchedEffect(config.playerCount) {
        val currentNames = config.playerNames
        val newNames = (0 until config.playerCount).map {
            currentNames.getOrNull(it) ?: "Player ${it + 1}"
        }
        if (newNames != currentNames) {
            config = config.copy(playerNames = newNames)
        }
    }

    // Handle hardware back button
    DisposableEffect(Unit) {
        // This is a placeholder for platform-specific back press handling
        onDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reaction Cascade") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    // Player Count Slider
                    Text("Players: ${config.playerCount}")
                    Slider(
                        value = config.playerCount.toFloat(),
                        onValueChange = { config = config.copy(playerCount = it.toInt()) },
                        valueRange = 2f..8f,
                        steps = 5
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Player Name Inputs
                itemsIndexed(config.playerNames) { index, name ->
                    OutlinedTextField(
                        value = name,
                        onValueChange = { newName ->
                            val newNames = config.playerNames.toMutableList()
                            newNames[index] = newName
                            config = config.copy(playerNames = newNames)
                        },
                        label = { Text("Player ${index + 1} Name") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Grid Width Slider
                    Text("Grid Width: ${config.gridWidth}")
                    Slider(
                        value = config.gridWidth.toFloat(),
                        onValueChange = { config = config.copy(gridWidth = it.toInt()) },
                        valueRange = 4f..12f,
                        steps = 7
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    // Grid Height Slider
                    Text("Grid Height: ${config.gridHeight}")
                    Slider(
                        value = config.gridHeight.toFloat(),
                        onValueChange = { config = config.copy(gridHeight = it.toInt()) },
                        valueRange = 4f..16f,
                        steps = 11
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    // 3D Toggle
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Enable 3D Molecules")
                        Spacer(modifier = Modifier.width(8.dp))
                        Checkbox(
                            checked = config.enable3D,
                            onCheckedChange = { config = config.copy(enable3D = it) }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    // Dark Mode Toggle
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Enable Dark Mode")
                        Spacer(modifier = Modifier.width(8.dp))
                        Checkbox(
                            checked = config.darkMode,
                            onCheckedChange = { newDarkMode ->
                                config = config.copy(darkMode = newDarkMode)
                                // Notify parent to update theme immediately
                                onThemeChanged(newDarkMode)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                item {
                    Button(
                        onClick = { onStartGame(config) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Game")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Footer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Created by Yogesh Paliyal with ❤️ from India", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
