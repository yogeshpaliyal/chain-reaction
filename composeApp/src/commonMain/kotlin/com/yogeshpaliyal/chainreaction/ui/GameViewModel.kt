package com.yogeshpaliyal.chainreaction.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.yogeshpaliyal.chainreaction.DefaultPlayerColors
import com.yogeshpaliyal.chainreaction.game.GameState
import com.yogeshpaliyal.chainreaction.game.Player
import com.yogeshpaliyal.chainreaction.game.advanceTurn
import com.yogeshpaliyal.chainreaction.game.cleanupAnimationState
import com.yogeshpaliyal.chainreaction.game.createInitialGrid
import com.yogeshpaliyal.chainreaction.game.hasPendingExplosions
import com.yogeshpaliyal.chainreaction.game.placeMolecule
import com.yogeshpaliyal.chainreaction.game.runChainReactionStep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val gameConfig: GameConfig) {

    private val _gameState = MutableStateFlow(createInitialState())
    val gameState = _gameState.asStateFlow()

    private val _isAnimating = MutableStateFlow(false)
    val isAnimating = _isAnimating.asStateFlow()

    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    fun onCellClick(x: Int, y: Int) {
        if (_isAnimating.value) return

        viewModelScope.launch {
            _isAnimating.value = true

            // 1. Place the initial molecule
            var currentState = placeMolecule(_gameState.value, x, y)
            _gameState.value = currentState

            // 2. Run the chain reaction animation loop
            var explosionLevel = 0
            while (hasPendingExplosions(currentState)) {
                // Run one level of explosions
                currentState = runChainReactionStep(currentState, explosionLevel)
                _gameState.value = currentState

                // Wait for the animation of this level to complete
                delay(300) // Adjust this delay as needed for your animation timing

                // Clean up the animation flags for the current level before the next
                currentState = cleanupAnimationState(currentState)
                _gameState.value = currentState

                explosionLevel++
            }

            // 3. Advance the turn
            _gameState.value = advanceTurn(currentState)

            _isAnimating.value = false
        }
    }

    fun restartGame() {
        _gameState.value = createInitialState()
    }

    private fun createInitialState(): GameState {
        return GameState(
            grid = createInitialGrid(gameConfig.gridWidth, gameConfig.gridHeight),
            players = (0 until gameConfig.playerCount).map {
                Player(it, gameConfig.playerNames[it], DefaultPlayerColors[it])
            },
            currentPlayerIndex = 0,
            playerTurns = (0 until gameConfig.playerCount).associate { it to 0 }
        )
    }
}
