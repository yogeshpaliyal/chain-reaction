package com.yogeshpaliyal.chainreaction.game

import androidx.compose.ui.graphics.Color

// --- Core Game Data Classes ---
data class Player(val id: Int, val name: String, val color: Color)

enum class CellType { CORNER, EDGE, INNER }

data class Cell(
    val x: Int,
    val y: Int,
    val owner: Player?,
    val molecules: Int,
    val captureAnimation: Boolean = false,
    val previousOwner: Player? = null,
    val explosionLevel: Int = 0,  // Level of cascade for animation timing
    val isExploding: Boolean = false,
    val explodingToPositions: List<Pair<Int, Int>> = emptyList(),
    val receivingExplosion: Boolean = false,
    val explosionSourcePosition: Pair<Int, Int>? = null,
    val animationCompleted: Boolean = false
)

// --- Game State ---
data class GameState(
    val grid: List<List<Cell>>,
    val players: List<Player>,
    val currentPlayerIndex: Int,
    val isGameOver: Boolean = false,
    val winner: Player? = null,
    val playerTurns: Map<Int, Int> = emptyMap() // playerId to turns taken
)
