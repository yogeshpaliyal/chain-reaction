package com.yogeshpaliyal.chainreaction.game

// --- Game Logic Functions ---
fun getCellType(x: Int, y: Int, width: Int, height: Int): CellType = when {
    (x == 0 || x == width - 1) && (y == 0 || y == height - 1) -> CellType.CORNER
    x == 0 || x == width - 1 || y == 0 || y == height - 1 -> CellType.EDGE
    else -> CellType.INNER
}

fun cellCapacity(type: CellType): Int = when (type) {
    CellType.CORNER -> 1
    CellType.EDGE -> 2
    CellType.INNER -> 3
}

fun createInitialGrid(width: Int, height: Int): List<List<Cell>> =
    List(height) { y ->
        List(width) { x ->
            Cell(x, y, null, 0)
        }
    }

fun getAdjacentCells(x: Int, y: Int, width: Int, height: Int): List<Pair<Int, Int>> =
    listOf(
        x to y - 1, x to y + 1, x - 1 to y, x + 1 to y
    ).filter { (nx, ny) -> nx in 0 until width && ny in 0 until height }

fun placeMoleculeAndResolve(state: GameState, x: Int, y: Int): GameState {
    val cell = state.grid[y][x]
    val currentPlayer = state.players[state.currentPlayerIndex]

    if (cell.owner != null && cell.owner != currentPlayer) return state

    val updatedGrid = state.grid.map { it.toMutableList() }
    updatedGrid[y][x] = cell.copy(
        owner = currentPlayer,
        molecules = cell.molecules + 1,
        previousOwner = cell.owner
    )

    val turns = state.playerTurns.toMutableMap()
    turns[currentPlayer.id] = (turns[currentPlayer.id] ?: 0) + 1

    var newState = state.copy(grid = updatedGrid.map { it.toList() }, playerTurns = turns)
    val width = newState.grid[0].size
    val height = newState.grid.size

    // Process explosions in levels to create proper cascade animation
    var explosionLevel = 0
    var explosionsOccurred = true

    while (explosionsOccurred) {
        val cellsToExplode = newState.grid.flatten().filter {
            it.molecules > cellCapacity(getCellType(it.x, it.y, width, height))
        }

        if (cellsToExplode.isEmpty()) {
            explosionsOccurred = false
            continue
        }

        val currentGrid = newState.grid.map { it.toMutableList() }

        // Process each exploding cell
        cellsToExplode.forEach { explodingCell ->
            val owner = explodingCell.owner
            val adjacentCells = getAdjacentCells(explodingCell.x, explodingCell.y, width, height)

            // Mark the exploding cell
            currentGrid[explodingCell.y][explodingCell.x] = explodingCell.copy(
                owner = null,
                molecules = 0,
                isExploding = true,
                explodingToPositions = adjacentCells,
                previousOwner = owner,
                explosionLevel = explosionLevel
            )

            // Update adjacent cells that receive molecules
            adjacentCells.forEach { (nx, ny) ->
                val adjCell = currentGrid[ny][nx]
                val wasCaptured = adjCell.owner != null && adjCell.owner != owner

                currentGrid[ny][nx] = adjCell.copy(
                    owner = owner,
                    molecules = adjCell.molecules + 1,
                    captureAnimation = wasCaptured,
                    previousOwner = if (wasCaptured) adjCell.owner else null,
                    receivingExplosion = true,
                    explosionSourcePosition = explodingCell.x to explodingCell.y,
                    explosionLevel = explosionLevel + 1
                )
            }
        }

        newState = newState.copy(grid = currentGrid.map { it.toList() })
        explosionLevel++
    }

    return newState
}

fun cleanupAnimationState(state: GameState): GameState {
    val cleanedGrid = state.grid.map { row ->
        row.map { cell ->
            cell.copy(
                isExploding = false,
                explodingToPositions = emptyList(),
                receivingExplosion = false,
                explosionSourcePosition = null,
                captureAnimation = false,
                previousOwner = null
            )
        }
    }
    return state.copy(grid = cleanedGrid)
}

fun advanceTurn(state: GameState): GameState {
    // Check for winner only after all players have had at least one turn
    val allPlayersHavePlayed = state.players.all { (state.playerTurns[it.id] ?: 0) > 0 }
    if (allPlayersHavePlayed) {
        val activePlayers = state.players.filter { p -> state.grid.flatten().any { it.owner == p } }
        if (activePlayers.size <= 1) {
            return state.copy(isGameOver = true, winner = activePlayers.firstOrNull())
        }
    }

    var nextPlayerIndex = (state.currentPlayerIndex + 1) % state.players.size

    // Skip players who have been eliminated
    while (allPlayersHavePlayed && state.grid.flatten().none { it.owner == state.players[nextPlayerIndex] }) {
        nextPlayerIndex = (nextPlayerIndex + 1) % state.players.size
        // This check prevents an infinite loop if only one player is left.
        if (nextPlayerIndex == state.currentPlayerIndex) {
            val activePlayers = state.players.filter { p -> state.grid.flatten().any { it.owner == p } }
            return state.copy(isGameOver = true, winner = activePlayers.firstOrNull())
        }
    }

    return state.copy(currentPlayerIndex = nextPlayerIndex)
}
