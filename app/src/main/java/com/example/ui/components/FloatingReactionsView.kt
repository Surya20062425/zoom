package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FloatingEmoji

@Composable
fun FloatingReactionsOverlay(
    floatingEmojis: List<FloatingEmoji>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val maxHeightPx = maxHeight

        floatingEmojis.forEach { reaction ->
            val transition = rememberInfiniteTransition(label = "emoji_float_${reaction.id}")
            val progress by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2400, easing = LinearOutSlowInEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "progress_${reaction.id}"
            )

            val yOffset = maxHeightPx * (1f - progress * 0.9f)
            val xOffset = maxWidth * reaction.startXFraction
            val alpha = (1f - progress * 0.7f).coerceIn(0f, 1f)
            val scale = 0.8f + progress * 0.5f

            Box(
                modifier = Modifier
                    .offset(x = xOffset, y = yOffset)
                    .alpha(alpha)
            ) {
                Text(
                    text = reaction.emoji,
                    fontSize = (32 * scale).sp
                )
            }
        }
    }
}
