package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DrawingPoint
import com.example.data.model.WhiteboardStroke
import com.example.ui.theme.ZoomBlue

@Composable
fun InteractiveWhiteboard(
    strokes: List<WhiteboardStroke>,
    onAddStroke: (WhiteboardStroke) -> Unit,
    onClear: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedColor by remember { mutableStateOf(Color(0xFF0E71EB)) }
    var currentStrokePoints by remember { mutableStateOf(listOf<DrawingPoint>()) }

    val colorOptions = listOf(
        Color(0xFF0E71EB), // Zoom Blue
        Color(0xFFE53935), // Red
        Color(0xFF43A047), // Green
        Color(0xFFFFB300), // Amber
        Color(0xFF1E2124)  // Dark
    )

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Whiteboard Header toolbar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF4F6F9))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Zoom Whiteboard",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E2124)
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    // Color palette
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        colorOptions.forEach { color ->
                            val isSelected = selectedColor == color
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { selectedColor = color }
                                    .then(
                                        if (isSelected) {
                                            Modifier.border(2.dp, Color(0xFF1E2124), CircleShape)
                                        } else Modifier
                                    )
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.testTag("whiteboard_clear")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear",
                            tint = Color(0xFF6E7687)
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("whiteboard_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF1E2124)
                        )
                    }
                }
            }

            // Drawing Area Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color(0xFFFAFBFC))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentStrokePoints = listOf(DrawingPoint(offset.x, offset.y))
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentStrokePoints = currentStrokePoints + DrawingPoint(change.position.x, change.position.y)
                            },
                            onDragEnd = {
                                if (currentStrokePoints.isNotEmpty()) {
                                    val stroke = WhiteboardStroke(
                                        points = currentStrokePoints,
                                        colorHex = (selectedColor.value shr 32).toLong(),
                                        strokeWidth = 6f
                                    )
                                    onAddStroke(stroke)
                                    currentStrokePoints = emptyList()
                                }
                            }
                        )
                    }
            ) {
                // Draw existing saved strokes
                strokes.forEach { stroke ->
                    if (stroke.points.size > 1) {
                        val path = Path().apply {
                            moveTo(stroke.points[0].x, stroke.points[0].y)
                            for (i in 1 until stroke.points.size) {
                                lineTo(stroke.points[i].x, stroke.points[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = Color(stroke.colorHex),
                            style = Stroke(
                                width = stroke.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                // Draw live current in-progress stroke
                if (currentStrokePoints.size > 1) {
                    val livePath = Path().apply {
                        moveTo(currentStrokePoints[0].x, currentStrokePoints[0].y)
                        for (i in 1 until currentStrokePoints.size) {
                            lineTo(currentStrokePoints[i].x, currentStrokePoints[i].y)
                        }
                    }
                    drawPath(
                        path = livePath,
                        color = selectedColor,
                        style = Stroke(
                            width = 6f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }
}
