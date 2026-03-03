package com.example.uedfocuskeeper.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTimerCanvas(
    timeLeftInSeconds: Int,
    totalTimeInSeconds: Int,
    modifier: Modifier = Modifier,
    circleSize: Dp = 250.dp
) {
    val progress = if (totalTimeInSeconds > 0) {
        timeLeftInSeconds.toFloat() / totalTimeInSeconds.toFloat()
    } else 0f

    val sweepAngle = progress * 360f
    val minutes = timeLeftInSeconds / 60
    val seconds = timeLeftInSeconds % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)
    val primaryColor = Color(0xFF0056A0)
    val trackColor = Color(0xFFE1F5FE)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(circleSize)
    ) {

        Canvas(modifier = Modifier.size(circleSize)) {

            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Text(
            text = timeString,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}