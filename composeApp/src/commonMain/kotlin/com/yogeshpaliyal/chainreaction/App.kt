package com.yogeshpaliyal.chainreaction

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.yogeshpaliyal.chainreaction.game.Player
import com.yogeshpaliyal.chainreaction.ui.ChainReactionGame
import com.yogeshpaliyal.chainreaction.ui.GameConfig
import com.yogeshpaliyal.chainreaction.ui.GameSetupScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

sealed class Screen {
    data object Setup : Screen()
    data class Game(val config: GameConfig) : Screen()
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var screen by remember { mutableStateOf<Screen>(Screen.Setup) }

        when (val s = screen) {
            is Screen.Setup -> {
                GameSetupScreen(onStartGame = { config ->
                    screen = Screen.Game(config)
                })
            }
            is Screen.Game -> {
                val players = (0 until s.config.playerCount).map {
                    Player(it, s.config.playerNames[it], DefaultPlayerColors[it])
                }
                ChainReactionGame(
                    gridWidth = s.config.gridWidth,
                    gridHeight = s.config.gridHeight,
                    players = players,
                    enable3D = s.config.enable3D,
                    onGameEnd = { screen = Screen.Setup }
                )
            }
        }
    }
}

val DefaultPlayerColors = listOf(
    Color.Red,
    Color(0xFF388E3C), // Green
    Color(0xFF1976D2), // Blue
    Color(0xFFFBC02D),  // Yellow
    Color(0xFFF57C00), // Orange
    Color.Cyan,
    Color.Magenta,
    Color(0xFFE91E63) // Pink
)
