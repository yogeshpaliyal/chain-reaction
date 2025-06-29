package com.yogeshpaliyal.chainreaction.ui

import androidx.compose.animation.core.Animatable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun AtomsMoleculesView(
    count: Int,
    color: Color,
    enable3D: Boolean,
    isCapturing: Boolean = false,
    previousColor: Color? = null,
    size: Dp = 32.dp // Default size, now customizable
) {
    // Calculate radius based on container size
    val radius = size * 0.18f // Relative radius based on container size

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

    // Remember original color and animate to new color when capturing
    var currentColor by remember(color, isCapturing) {
        mutableStateOf(if (isCapturing && previousColor != null) previousColor else color)
    }

    // Pulse animation for captured molecules
    val pulseScale = remember { Animatable(1f) }

    // Glow effect for captures
    val glowAlpha = remember { Animatable(0f) }

    // Color transition for captures
    LaunchedEffect(isCapturing, color) {
        if (isCapturing && previousColor != null) {
            // Start with previous color, animate to new color
            currentColor = previousColor

            // Pulse animation
            pulseScale.animateTo(
                targetValue = 1.5f,
                animationSpec = tween(durationMillis = 200)
            )
            pulseScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200)
            )

            // Glow effect
            glowAlpha.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(durationMillis = 150)
            )
            glowAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 450)
            )

            // Transition to new color
            currentColor = color
        }
    }

    // Animate scale for pop-in effect
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 350),
        label = "atomScale"
    )

    Canvas(modifier = Modifier.size(size)) {
        val canvasCenter = Offset(size.toPx() / 2, size.toPx() / 2)
        val radiusPx = radius.toPx()

        // Define positions relative to the center - scaled based on container size
        val atomPositions = when (count) {
            1 -> listOf(canvasCenter)
            2 -> listOf(canvasCenter - Offset(radiusPx * 1.7f, 0f), canvasCenter + Offset(radiusPx * 1.7f, 0f))
            else -> listOf(
                canvasCenter + Offset(0f, -radiusPx * 1.8f),
                canvasCenter + Offset(-radiusPx * 1.6f, radiusPx * 0.9f),
                canvasCenter + Offset(radiusPx * 1.6f, radiusPx * 0.9f)
            )
        }

        // Draw glow effect if capturing
        if (isCapturing && glowAlpha.value > 0) {
            drawCircle(
                color = color.copy(alpha = glowAlpha.value),
                radius = size.toPx() * 0.45f,
                center = canvasCenter
            )
        }

        withTransform({
            rotate(rotation.value)
            scale(scale * pulseScale.value)
        }) {
            for (i in 0 until min(count, 3)) {
                val pos = atomPositions.getOrNull(i) ?: canvasCenter
                if (enable3D) {
                    // Simulate 3D with radial gradient and highlight
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(currentColor.copy(alpha = 0.95f), currentColor.copy(alpha = 0.7f), Color.White.copy(alpha = 0.2f)),
                            center = pos + Offset(radiusPx * 0.3f, radiusPx * 0.3f),
                            radius = radiusPx * 1.2f
                        ),
                        radius = radiusPx,
                        center = pos
                    )
                    // Highlight
                    drawCircle(
                        color = Color.White.copy(alpha = 0.35f),
                        radius = radiusPx * 0.4f,
                        center = pos - Offset(radiusPx * 0.3f, radiusPx * 0.3f)
                    )
                } else {
                    drawCircle(
                        color = currentColor,
                        radius = radiusPx,
                        center = pos
                    )
                }
            }
        }

        if (count > 3) {
            drawCircle(
                color = Color.Black,
                radius = radiusPx * 0.5f * scale,
                center = Offset(size.toPx() - radiusPx, radiusPx)
            )
        }
    }
}
