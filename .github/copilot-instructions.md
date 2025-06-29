Product Requirements Document: Chain Reaction Game (Compose Multiplatform)
1. Introduction
   This document outlines the product requirements for a new digital game, tentatively titled "Reaction Cascade" (or similar), inspired by the classic "Chain Reaction" game. The goal is to create an engaging, customizable, and visually appealing experience for players, allowing for both strategic depth and casual fun, developed using Compose Multiplatform to ensure a consistent experience across multiple platforms. This PRD details the core features, user experience, and technical considerations required for the game's development.

2. Game Overview
   "Reaction Cascade" is a turn-based strategy game played on a grid, where players take turns placing "molecules" into cells. When a cell reaches its capacity, it "explodes," distributing its molecules to adjacent cells and converting any opponent's molecules in those cells to the current player's color. The last player remaining on the board wins.

3. Target Audience
   Casual gamers looking for a simple yet engaging strategy game.

Players who enjoy turn-based board games and puzzles.

Users who appreciate customizable game experiences.

4. Key Features
   4.1. Core Gameplay Mechanics
   Grid-based Play: The game will be played on a customizable grid (e.g., 8x8, 10x10).

Turn-based: Players take turns placing molecules.

Molecule Placement: Players click/tap on an empty or friendly cell to add one molecule.

Capacity & Explosion:

Corner cells: Capacity 1

Edge cells: Capacity 2

Inner cells: Capacity 3

When a cell reaches capacity, it explodes, sending one molecule to each adjacent cell.

Explosions trigger chain reactions if adjacent cells also reach capacity.

Molecule Conversion: When an explosion occurs, any opponent's molecules in the affected adjacent cells are converted to the current player's color.

Player Elimination: A player is eliminated when they have no molecules left on the board.

Winning Condition: The last player remaining on the board wins.

4.2. Player Configuration
Number of Players:

Support for 2 to 4 players.

Option for AI opponents to fill empty player slots.

Player Colors: Players can choose their preferred color from a predefined palette.

Optional Player Names: Users can optionally enter custom names for each player (e.g., "Player 1", "AI Bob"). If no name is provided, a default name will be used.

4.3. Grid Configuration
Customizable Grid Size:

Users can select predefined grid sizes (e.g., Small, Medium, Large).

Option to manually set custom dimensions (e.g., Width x Height).

Cell Capacity Visualization: Clearly indicate the capacity of each cell type (corner, edge, inner).

4.4. Visual & Aesthetic Options
3D Molecules (Optional Toggle):

Users can enable or disable 3D rendering for molecules.

If disabled, molecules will be represented by 2D shapes (e.g., circles).

This feature is crucial for performance optimization on lower-end devices and user preference.

Visual Effects:

Smooth animations for molecule placement, movement, and explosions.

Distinct visual cues for cell capacity and impending explosions.

Sound Effects (SFX):

Subtle sounds for molecule placement, explosions, and game events.

Option to mute/unmute SFX.

4.5. User Interface (UI)
Start Screen:

"New Game" button.

"Options" button.

"How to Play" (rules explanation) button.

Game Setup Screen:

Dropdown/sliders for number of players.

Player name input fields.

Color pickers for each player.

Grid size selection.

Toggle for "Enable 3D Molecules".

"Start Game" button.

In-Game UI:

Display current player's turn.

Scoreboard showing remaining molecules for each player.

"Restart Game" button.

"Exit to Main Menu" button.

Pause menu.

End Game Screen:

Announce the winner.

Option to "Play Again" or "Return to Main Menu".

5. Technical Requirements
   Framework: Compose Multiplatform (Kotlin).

Target Platforms: Android, iOS, Desktop (Windows, macOS, Linux), with Web (JS/WASM) support planned for a later phase.

Language: Kotlin.

Graphics:

2D rendering will leverage Compose Multiplatform's built-in drawing capabilities (Skia backend for Desktop/Android/iOS).

For optional 3D molecules, investigation into integrating a 3D rendering library (e.g., OpenGL/Vulkan bindings if available and performant with Compose Multiplatform, or a dedicated 3D library compatible with Kotlin Multiplatform) will be required. Alternatively, 3D effects could be simulated with advanced 2D rendering techniques within Compose if true 3D integration proves too complex for the initial scope.

Responsiveness: The UI must be fully responsive, adapting to various screen sizes and orientations across all target platforms (mobile, tablet, desktop). Compose's declarative UI approach will facilitate this.

Performance: Optimize animations, game logic, and rendering for smooth gameplay on a wide range of devices, considering the capabilities of each target platform.

State Management: Utilize Compose's reactive state management paradigms (e.g., MutableState, ViewModel for larger state) to efficiently manage game state, player data, and grid configurations.

Asset Management: Manage game assets (e.g., molecule sprites/models, sound files) in a multiplatform-compatible manner.

No External APIs: The game will be self-contained, with no reliance on external APIs for core functionality (except for potential future additions like platform-specific services or multiplayer if scoped later).

6. Future Considerations (Out of Scope for initial release)
   Multiplayer functionality (online play).

Additional game modes (e.g., puzzle mode, timed mode).

More complex grid shapes.

Power-ups or special cells.

Persistent high scores (potentially utilizing platform-specific storage or a shared backend).

7. Metrics for Success
   User Engagement: Number of game sessions, average session duration.

Feature Adoption: Usage rate of 3D molecule toggle, custom grid sizes.

Positive Feedback: User reviews and direct feedback on gameplay and experience across all platforms.

Stability: Minimal bugs and crashes on all target platforms.

Cross-Platform Consistency: High degree of visual and functional consistency across Android, iOS, and Desktop versions.