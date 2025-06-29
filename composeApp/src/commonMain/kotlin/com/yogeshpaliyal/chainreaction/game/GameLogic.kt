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

fun placeMolecule(
    state: GameState,
    x: Int,
    y: Int
): GameState {
    if (state.isGameOver) return state
    state.grid[0].size
    state.grid.size
    val cell = state.grid[y][x]
    val currentPlayer = state.players[state.currentPlayerIndex]
    if (cell.owner != null && cell.owner != currentPlayer) return state // Invalid move
    // Place molecule
    val updatedGrid = state.grid.map { it.toMutableList() }
    updatedGrid[y][x] = cell.copy(
        owner = currentPlayer,
        molecules = cell.molecules + 1
    )
    // Update player turn count
    val updatedTurns = state.playerTurns.toMutableMap()
    updatedTurns[currentPlayer.id] = (updatedTurns[currentPlayer.id] ?: 0) + 1
    return resolveExplosions(
        state.copy(grid = updatedGrid.map { it.toList() }, playerTurns = updatedTurns),
        x,
        y
    ).let { nextState ->
        // Only eliminate players who have played at least one turn
        val activePlayers = nextState.players.filter { p ->
            (nextState.playerTurns[p.id] ?: 0) > 0 &&
                    nextState.grid.flatten().any { it.owner == p }
        }
        // Only declare winner if all players have played at least one turn
        val allPlayed = nextState.players.all { (nextState.playerTurns[it.id] ?: 0) > 0 }
        if (allPlayed && activePlayers.size == 1) {
            nextState.copy(isGameOver = true, winner = activePlayers.first())
        } else {
            nextState.copy(currentPlayerIndex = (state.currentPlayerIndex + 1) % state.players.size)
        }
    }
}

fun resolveExplosions(state: GameState, x: Int, y: Int, level: Int = 0): GameState {
    val width = state.grid[0].size
    val height = state.grid.size
    val cell = state.grid[y][x]
    val type = getCellType(x, y, width, height)
    val capacity = cellCapacity(type)
    if (cell.molecules <= capacity) return state
    val updatedGrid = state.grid.map { it.toMutableList() }
    updatedGrid[y][x] = cell.copy(owner = null, molecules = 0)
    val owner = cell.owner

    // Process adjacent cells
    getAdjacentCells(x, y, width, height).forEach { (nx, ny) ->
        val adjCell = updatedGrid[ny][nx]
        // Track ownership changes with animation flag and include explosion level
        val isCaptured = adjCell.owner != null && adjCell.owner != owner
        updatedGrid[ny][nx] = adjCell.copy(
            owner = owner,
            molecules = adjCell.molecules + 1,
            captureAnimation = isCaptured,
            previousOwner = if (isCaptured) adjCell.owner else null,
            explosionLevel = level + 1  // Increment level for cascading effect
        )
    }

    var newState = state.copy(grid = updatedGrid.map { it.toList() })

    // Recursively resolve further explosions
    getAdjacentCells(x, y, width, height).forEach { (nx, ny) ->
        val c = newState.grid[ny][nx]
        val t = getCellType(nx, ny, width, height)
        val cap = cellCapacity(t)
        if (c.molecules > cap) {
            newState = resolveExplosions(newState, nx, ny, level + 1)
        }
    }

    return newState
}
