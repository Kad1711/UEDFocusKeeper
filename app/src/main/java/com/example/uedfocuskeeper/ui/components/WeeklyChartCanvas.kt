package com.example.uedfocuskeeper.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uedfocuskeeper.data.StudySession
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeeklyChartCanvas(sessions: List<StudySession>, modifier: Modifier = Modifier) {

    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
    val last7Days = mutableMapOf<String, Int>()

    for (i in 6 downTo 0) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -i)
        last7Days[dateFormat.format(cal.time)] = 0
    }

    sessions.forEach { session ->
        val dateStr = dateFormat.format(Date(session.completionDate))
        if (last7Days.containsKey(dateStr)) {
            last7Days[dateStr] = last7Days[dateStr]!! + session.durationMinutes
        }
    }

    val maxMinutes = last7Days.values.maxOrNull()?.takeIf { it > 0 } ?: 1
    val barColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Thống kê 7 Ngày Qua (Phút)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Vẽ Canvas
        Canvas(modifier = Modifier.fillMaxWidth().height(150.dp).padding(horizontal = 16.dp)) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val barWidth = canvasWidth / (last7Days.size * 2f)

            last7Days.entries.forEachIndexed { index, entry ->
                val (_, minutes) = entry

                val barHeight = (minutes.toFloat() / maxMinutes) * canvasHeight
                // Tính toán tọa độ X để đặt cột
                val xOffset = index * (barWidth * 2) + barWidth / 2

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(xOffset, canvasHeight - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }
    }
}