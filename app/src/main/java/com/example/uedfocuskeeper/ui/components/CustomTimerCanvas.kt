package com.example.uedfocuskeeper.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTimerCanvas(
    timeLeftInSeconds: Int,
    totalTimeInSeconds: Int,
    modifier: Modifier = Modifier
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

        modifier = modifier
            .padding(16.dp)
            .sizeIn(maxWidth = 280.dp, maxHeight = 280.dp)
            .aspectRatio(1f)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()

            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(
            text = timeString,
            fontSize = 44.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}