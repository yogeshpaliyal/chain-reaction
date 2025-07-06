package com.yogeshpaliyal.chainreaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.yogeshpaliyal.chainreaction.game.Player
import com.yogeshpaliyal.chainreaction.ui.AppTheme
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
    var screen by remember { mutableStateOf<Screen>(Screen.Setup) }
    var showExitConfirmation by remember { mutableStateOf(false) }

    // Maintain separate state for setup screen dark mode for immediate theme changes
    var setupScreenDarkMode by remember { mutableStateOf(false) }

    // Get current screen config for theme
    val isDarkMode = when (val s = screen) {
        is Screen.Game -> s.config.darkMode
        is Screen.Setup -> setupScreenDarkMode // Use the separate state for setup
    }


    AppTheme(darkTheme = isDarkMode) {
        when (val s = screen) {
            is Screen.Setup -> {
                GameSetupScreen(
                    initialDarkMode = setupScreenDarkMode,
                    onThemeChanged = { newDarkMode ->
                        setupScreenDarkMode = newDarkMode
                    },
                    onStartGame = { config ->
                        screen = Screen.Game(config)
                    }
                )
            }
            is Screen.Game -> {
                ChainReactionGame(
                    gameConfig = s.config,
                    onGameEnd = {
                        // Update setup screen dark mode when returning to setup
                        setupScreenDarkMode = s.config.darkMode
                        screen = Screen.Setup
                    },
                    onBackPressed = { showExitConfirmation = true },
                    showExitConfirmation = showExitConfirmation,
                    onExitConfirmed = {
                        // Update setup screen dark mode when returning to setup
                        setupScreenDarkMode = s.config.darkMode
                        showExitConfirmation = false
                        screen = Screen.Setup
                    },
                    onExitCancelled = { showExitConfirmation = false }
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
