# Reaction Cascade

A turn-based strategy game inspired by the classic "Chain Reaction" game, built with Kotlin and Compose Multiplatform.

![Game Screenshot](https://via.placeholder.com/800x450?text=Reaction+Cascade+Screenshot)

## 📱 Play Now

You can play the latest version of the game here: [Reaction Cascade Web Demo](https://yourusername.github.io/Chain-Reaction/) 

## 🎮 Game Overview

Reaction Cascade is a turn-based strategy game played on a customizable grid. Players take turns placing "molecules" into cells. When a cell reaches its capacity, it explodes, distributing its molecules to adjacent cells and converting any opponent's molecules to the current player's color. The last player remaining on the board wins.

### Rules

- **Turn-based**: Players take turns placing one molecule per turn
- **Cell Capacity**:
  - Corner cells: 1 molecule
  - Edge cells: 2 molecules
  - Inner cells: 3 molecules
- **Explosions**: When a cell reaches its capacity, it explodes, sending one molecule to each adjacent cell
- **Chain Reactions**: Explosions can trigger other explosions, creating chain reactions across the board
- **Molecule Conversion**: When an explosion reaches a cell with opponent molecules, they convert to the attacker's color
- **Winning**: The last player with molecules on the board wins

## ✨ Features

- **Customizable Players**: 2-8 players with custom names and colors
- **Adjustable Grid Size**: Customize the dimensions of your playing field
- **Stunning Visuals**: Choose between 3D molecules with effects or simpler 2D graphics
- **Dark Mode Support**: Play comfortably day or night with theme options
- **Responsive Design**: Plays well on any device or screen size
- **Cross-Platform**: Built with Compose Multiplatform for Android, iOS, Desktop, and Web

## 🚀 Getting Started

### Prerequisites

- JDK 17 or newer
- Android Studio Arctic Fox or newer (for Android development)
- Xcode 14+ (for iOS development)
- Gradle 8.0+

### Installation & Setup

1. Clone the repository
```bash
git clone https://github.com/yourusername/Chain-Reaction.git
cd Chain-Reaction
```

2. Run on Desktop
```bash
./gradlew composeApp:desktopRun
```

3. Run Web version
```bash
./gradlew composeApp:wasmJsBrowserDevelopmentRun
```

4. Run on Android
```bash
./gradlew composeApp:assembleDebug
```

5. Run on iOS (from macOS)
```bash
./gradlew composeApp:iosDeployIPhone
```

## 🏗️ Project Structure

- `/composeApp` - Shared Compose Multiplatform code
  - `/commonMain` - Platform-independent code (game logic, UI)
  - `/androidMain` - Android-specific code
  - `/iosMain` - iOS-specific code
  - `/desktopMain` - Desktop-specific code
  - `/wasmJsMain` - Web-specific code
- `/iosApp` - iOS application entry point

## 🔄 Continuous Deployment

This project uses GitHub Actions for automatic deployment to GitHub Pages. Every push to the main branch triggers a build and deploys the web version to GitHub Pages.

You can view the deployment workflow in `.github/workflows/deploy.yml`.

## 👨‍💻 Contributing

Contributions are welcome! Feel free to open issues or submit pull requests.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgements

Created by Yogesh Paliyal with ❤️ from India.

---

Built with [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) and [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform).
