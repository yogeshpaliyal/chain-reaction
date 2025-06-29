package com.yogeshpaliyal.chainreaction

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Chain Reaction",
    ) {
        App()
    }
}