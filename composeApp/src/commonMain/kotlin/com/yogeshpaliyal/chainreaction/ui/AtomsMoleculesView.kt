package com.yogeshpaliyal.chainreaction.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import kotlinx.coroutines.delay
import kotlin.math.min

@Composable
fun AtomsMoleculesView(
    count: Int,
    color: Color,
    enable3D: Boolean,
    isCapturing: Boolean = false,
    previousColor: Color? = null,
    size: Dp = 32.dp, // Default size, now customizable
    explosionLevel: Int = 0, // Added explosion level for cascading effect
    isExploding: Boolean = false,
    explodingToPositions: List<Pair<Int, Int>> = emptyList(),
    cellPosition: Pair<Int, Int>? = null,
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
    var currentColor by remember(color, isCapturing, explosionLevel) {
        mutableStateOf(if (isCapturing && previousColor != null) previousColor else color)
    }

    // Pulse animation for captured molecules
    val pulseScale = remember { Animatable(1f) }

    // Glow effect for captures
    val glowAlpha = remember { Animatable(0f) }

    val explosionProgress = remember { Animatable(0f) }

    LaunchedEffect(isExploding) {
        if (isExploding) {
            explosionProgress.snapTo(0f)
            explosionProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        }
    }

    // Animation has started flag to prevent re-triggering
    var animationStarted by remember { mutableStateOf(false) }

    // Color transition for captures with cascade delay based on explosion level
    LaunchedEffect(isCapturing, color, explosionLevel) {
        if (isCapturing && previousColor != null && !animationStarted) {
            animationStarted = true

            // Delay based on explosion level - creates cascading effect
            // Each level waits longer to start its animation
            val cascadeDelay = explosionLevel * 300L // 300ms per level
            delay(cascadeDelay)

            // Start with previous color
            currentColor = previousColor

            // Enhanced capture animation sequence

            // 1. Quick scale up
            pulseScale.animateTo(
                targetValue = 1.6f, // More dramatic expansion
                animationSpec = tween(durationMillis = 180)
            )

            // 2. Glow effect peaks as molecule expands
            glowAlpha.animateTo(
                targetValue = 0.8f,
                animationSpec = tween(durationMillis = 130)
            )

            // 3. Scale down with color change
            pulseScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 220)
            )

            // 4. Fade glow
            glowAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 350)
            )

            // Transition to new color - happens during scale down
            currentColor = color
        }
    }

    // Animate scale for pop-in effect
    val popInScale by animateFloatAsState(
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



        if (isExploding) {
            val progress = explosionProgress.value
            if (progress > 0f && progress < 1f) { // Draw only during the animation
                explodingToPositions.forEach { targetPos ->
                    val direction = Offset(
                        x = (targetPos.first - (cellPosition?.first ?: 0)).toFloat(),
                        y = (targetPos.second - (cellPosition?.second ?: 0)).toFloat()
                    )

                    val distance = size.toPx()
                    val animatedOffset = canvasCenter + (direction * distance * progress)

                    drawCircle(
                        color = color,
                        radius = radiusPx,
                        center = animatedOffset
                    )
                }
            }
        } else if (count > 0) {
            withTransform({
                rotate(rotation.value)
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
                    radius = radiusPx * 0.5f * popInScale,
                    center = Offset(size.toPx() - radiusPx, radiusPx)
                )
            }
        }
    }
}
