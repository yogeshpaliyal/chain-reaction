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
    size: Dp = 32.dp,
    explosionLevel: Int = 0,
    isExploding: Boolean = false,
    explodingToPositions: List<Pair<Int, Int>> = emptyList(),
    cellPosition: Pair<Int, Int>? = null,
) {
    // Calculate radius based on container size
    val radius = size * 0.18f

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

    // Glow effect for captures and explosions
    val glowAlpha = remember { Animatable(0f) }

    // Explosion animation progress (0f to 1f)
    val explosionProgress = remember { Animatable(0f) }

    // Track explosion animation state
    var isCurrentlyExploding by remember { mutableStateOf(false) }

    // Animation has started flag to prevent re-triggering
    var animationStarted by remember { mutableStateOf(false) }

    // Enhanced explosion animation with proper timing
    LaunchedEffect(isExploding) {
        if (isExploding && explodingToPositions.isNotEmpty() && !isCurrentlyExploding) {
            isCurrentlyExploding = true

            // Add delay based on explosion level for cascade effect
            delay(explosionLevel * 1500L)


            // Explosion glow
            glowAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 150)
            )

            // Atoms fly out animation
            explosionProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 150,
                    easing = FastOutSlowInEasing
                )
            )

            // Fade out glow
            glowAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 150)
            )

            // Reset after animation
            delay(150)
            isCurrentlyExploding = false
            explosionProgress.snapTo(0f)
        }
    }

    // Color transition for captures with cascade delay based on explosion level
    LaunchedEffect(isCapturing, color, explosionLevel) {
        if (isCapturing && previousColor != null && previousColor != color && !animationStarted) {
            animationStarted = true

            // Delay based on explosion level - creates cascading effect
            val cascadeDelay = explosionLevel * 200L
            delay(cascadeDelay)

            // Start with previous color
            currentColor = previousColor

            // Enhanced capture animation sequence
            // 1. Quick scale up
//            pulseScale.animateTo(
//                targetValue = 1.5f,
//                animationSpec = tween(durationMillis = 150)
//            )

            // 2. Glow effect peaks as molecule expands
            glowAlpha.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(durationMillis = 100)
            )

            // 3. Scale down with color change
            currentColor = color
//            pulseScale.animateTo(
//                targetValue = 1f,
//                animationSpec = tween(durationMillis = 200)
//            )

            // 4. Fade glow
            glowAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 250)
            )

            animationStarted = false
        }
    }

    // Animate scale for pop-in effect
    val popInScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 300),
        label = "atomScale"
    )

    Canvas(modifier = Modifier.size(size)) {
        val canvasCenter = Offset(size.toPx() / 2, size.toPx() / 2)
        val radiusPx = radius.toPx()

        // Define positions relative to the center - scaled based on container size
        val atomPositions = when (count) {
            1 -> listOf(canvasCenter)
            2 -> listOf(
                canvasCenter - Offset(radiusPx * 1.7f, 0f),
                canvasCenter + Offset(radiusPx * 1.7f, 0f)
            )
            else -> listOf(
                canvasCenter + Offset(0f, -radiusPx * 1.8f),
                canvasCenter + Offset(-radiusPx * 1.6f, radiusPx * 0.9f),
                canvasCenter + Offset(radiusPx * 1.6f, radiusPx * 0.9f)
            )
        }

        // Handle explosion animation
        if (isExploding && isCurrentlyExploding && explosionProgress.value > 0) {
            val progress = explosionProgress.value

            explodingToPositions.forEachIndexed { index, targetPos ->
                val direction = Offset(
                    x = (targetPos.first - (cellPosition?.first ?: 0)).toFloat(),
                    y = (targetPos.second - (cellPosition?.second ?: 0)).toFloat()
                )

                // Stagger atoms slightly for more realistic explosion
                val staggeredProgress = (progress - (index * 0.1f)).coerceIn(0f, 1f)
                val distance = size.toPx() * 1.2f
                val atomOffset = canvasCenter + (direction * distance * staggeredProgress)

                // Rotating atoms as they fly
                val rotationAngle = staggeredProgress * 360f

                withTransform({
                    rotate(rotationAngle, atomOffset)
                }) {
                    if (enable3D) {
                        // 3D exploding atom
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    currentColor.copy(alpha = 0.9f),
                                    currentColor.copy(alpha = 0.6f),
                                    Color.White.copy(alpha = 0.2f)
                                ),
                                center = atomOffset + Offset(radiusPx * 0.3f, radiusPx * 0.3f),
                                radius = radiusPx * 1.1f
                            ),
                            radius = radiusPx * 0.8f,
                            center = atomOffset
                        )
                    } else {
                        drawCircle(
                            color = currentColor.copy(alpha = 1f - staggeredProgress * 0.3f),
                            radius = radiusPx * 0.8f,
                            center = atomOffset
                        )
                    }
                }

                // Trail effect
                if (staggeredProgress > 0.2f) {
                    val trailAlpha = (1f - staggeredProgress) * 0.4f
                    val trailOffset = canvasCenter + (direction * distance * staggeredProgress * 0.7f)
                    drawCircle(
                        color = currentColor.copy(alpha = trailAlpha),
                        radius = radiusPx * 0.5f,
                        center = trailOffset
                    )
                }
            }
        } else if (count > 0) {
            // Normal molecule rendering
            withTransform({
                rotate(rotation.value)
                scale(
                    scaleX = popInScale * pulseScale.value,
                    scaleY = popInScale * pulseScale.value,
                    pivot = canvasCenter
                )
            }) {
                for (i in 0 until min(count, 3)) {
                    val pos = atomPositions.getOrNull(i) ?: canvasCenter
                    if (enable3D) {
                        // Simulate 3D with radial gradient and highlight
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    currentColor.copy(alpha = 0.95f),
                                    currentColor.copy(alpha = 0.7f),
                                    Color.White.copy(alpha = 0.2f)
                                ),
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
