package com.yogeshpaliyal.chainreaction.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun AtomsMoleculesView(count: Int, color: Color, enable3D: Boolean) {
    val radius = 6.dp

    // Animate rotation
    val rotation = rememberInfiniteTransition(label = "rotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_animation"
    )

    // Animate scale for pop-in effect
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 350), label = "atomScale"
    )

    Canvas(modifier = Modifier.size(32.dp)) {
        val canvasCenter = Offset(size.width / 2, size.height / 2)

        // Define positions relative to the center
        val atomPositions = when (count) {
            1 -> listOf(canvasCenter)
            2 -> listOf(canvasCenter - Offset(radius.toPx(), 0f), canvasCenter + Offset(radius.toPx(), 0f))
            else -> listOf(
                canvasCenter + Offset(0f, -radius.toPx() * 1.5f),
                canvasCenter + Offset(-radius.toPx() * 1.3f, radius.toPx() * 0.75f),
                canvasCenter + Offset(radius.toPx() * 1.3f, radius.toPx() * 0.75f)
            )
        }

        withTransform({
            rotate(degrees = rotation.value, pivot = canvasCenter)
            scale(scale, scale, pivot = canvasCenter)
        }) {
            for (i in 0 until min(count, 3)) {
                val pos = atomPositions.getOrNull(i) ?: canvasCenter
                if (enable3D) {
                    // Simulate 3D with radial gradient and highlight
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(color.copy(alpha = 0.95f), color.copy(alpha = 0.7f), Color.White.copy(alpha = 0.2f)),
                            center = pos + Offset(2f, 2f),
                            radius = radius.toPx() * 1.2f
                        ),
                        radius = radius.toPx(),
                        center = pos
                    )
                    // Highlight
                    drawCircle(
                        color = Color.White.copy(alpha = 0.35f),
                        radius = radius.toPx() * 0.4f,
                        center = pos - Offset(2f, 2f)
                    )
                } else {
                    drawCircle(
                        color = color,
                        radius = radius.toPx(),
                        center = pos
                    )
                }
            }
        }

        if (count > 3) {
            drawCircle(
                color = Color.Black,
                radius = 3.dp.toPx() * scale,
                center = Offset(size.width - 3.dp.toPx(), 3.dp.toPx())
            )
        }
    }
}

