package com.facialdataset.frontend.ui.components


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun OvalOverlay(
    isFaceDetected: Boolean,
    modifier: Modifier = Modifier
) {
    // Animación de color del óvalo: cyan si detecta cara, blanco si no
    val ovalColor by animateColorAsState(
        targetValue = if (isFaceDetected) Color(0xFF00E5FF) else Color(0x80FFFFFF),
        animationSpec = tween(300),
        label = "ovalColor"
    )

    // Animación de pulso cuando detecta cara
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = if (isFaceDetected) 1.03f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Animación de opacidad del borde exterior
    val glowAlpha by pulseAnim.animateFloat(
        initialValue = 0f,
        targetValue = if (isFaceDetected) 0.4f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth  = size.width
        val canvasHeight = size.height

        // Oscurecer todo el fondo
        drawRect(color = Color(0x99000000))

        val ovalWidth  = canvasWidth  * 0.65f * pulseScale
        val ovalHeight = canvasHeight * 0.42f * pulseScale
        val left       = (canvasWidth  - ovalWidth)  / 2f
        val top        = (canvasHeight - ovalHeight) / 2f - canvasHeight * 0.04f

        // Recorte transparente (hueco del óvalo)
        drawOval(
            color     = Color.Transparent,
            topLeft   = Offset(left, top),
            size      = Size(ovalWidth, ovalHeight),
            blendMode = BlendMode.Clear
        )

        // Borde glow exterior (solo cuando detecta cara)
        if (isFaceDetected) {
            drawOval(
                color   = Color(0xFF00E5FF).copy(alpha = glowAlpha),
                topLeft = Offset(left - 8.dp.toPx(), top - 8.dp.toPx()),
                size    = Size(ovalWidth + 16.dp.toPx(), ovalHeight + 16.dp.toPx()),
                style   = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Borde principal del óvalo
        drawOval(
            color   = ovalColor,
            topLeft = Offset(left, top),
            size    = Size(ovalWidth, ovalHeight),
            style   = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Esquinas decorativas (4 arcos en las esquinas)
        val cornerSize = 24.dp.toPx()
        val strokeW    = 4.dp.toPx()

        // Superior izquierda
        drawArc(
            color     = ovalColor,
            startAngle = 180f,
            sweepAngle = 45f,
            useCenter  = false,
            topLeft    = Offset(left, top),
            size       = Size(cornerSize * 2, cornerSize * 2),
            style      = Stroke(width = strokeW)
        )
        // Superior derecha
        drawArc(
            color      = ovalColor,
            startAngle = 270f,
            sweepAngle = 45f,
            useCenter  = false,
            topLeft    = Offset(left + ovalWidth - cornerSize * 2, top),
            size       = Size(cornerSize * 2, cornerSize * 2),
            style      = Stroke(width = strokeW)
        )
        // Inferior izquierda
        drawArc(
            color      = ovalColor,
            startAngle = 135f,
            sweepAngle = 45f,
            useCenter  = false,
            topLeft    = Offset(left, top + ovalHeight - cornerSize * 2),
            size       = Size(cornerSize * 2, cornerSize * 2),
            style      = Stroke(width = strokeW)
        )
        // Inferior derecha
        drawArc(
            color      = ovalColor,
            startAngle = 45f,
            sweepAngle = 45f,
            useCenter  = false,
            topLeft    = Offset(left + ovalWidth - cornerSize * 2, top + ovalHeight - cornerSize * 2),
            size       = Size(cornerSize * 2, cornerSize * 2),
            style      = Stroke(width = strokeW)
        )
    }
}