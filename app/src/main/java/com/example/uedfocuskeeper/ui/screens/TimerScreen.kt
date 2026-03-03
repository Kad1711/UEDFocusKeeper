package com.example.uedfocuskeeper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List // Đã đổi sang List để không bị lỗi thư viện
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uedfocuskeeper.ui.components.CustomTimerCanvas
import com.example.uedfocuskeeper.viewmodel.TimerStatus
import com.example.uedfocuskeeper.viewmodel.TimerViewModel
import androidx.compose.ui.graphics.Color
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    onNavigateToHistory: () -> Unit
) {
    // Khai báo context ở ngay đầu hàm Composable
    val context = LocalContext.current

    val courseName by viewModel.courseName.collectAsStateWithLifecycle()
    val timeLeft by viewModel.timeLeftInSeconds.collectAsStateWithLifecycle()
    val totalTime by viewModel.totalTimeInSeconds.collectAsStateWithLifecycle()
    val status by viewModel.timerStatus.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("UED Focus Keeper", fontWeight = FontWeight.Bold) },
                actions = {
                    // Nút bấm chuyển sang màn hình Lịch sử
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.List, contentDescription = "Xem Lịch sử")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OutlinedTextField(
                value = courseName,
                onValueChange = { viewModel.updateCourseName(it) },
                label = { Text("Tên môn học") },
                placeholder = { Text("Ví dụ: Lập trình Android") },
                modifier = Modifier.fillMaxWidth(),
                enabled = status == TimerStatus.IDLE
            )

            OutlinedTextField(
                value = (totalTime / 60).toString(),
                onValueChange = {
                    val mins = it.toIntOrNull() ?: 0
                    viewModel.updateTotalTime(mins)
                },
                label = { Text("Thời gian tập trung (phút)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = status == TimerStatus.IDLE
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTimerCanvas(
                timeLeftInSeconds = timeLeft,
                totalTimeInSeconds = totalTime
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                when (status) {
                    TimerStatus.IDLE, TimerStatus.PAUSED -> {
                        Button(
                            onClick = { viewModel.startTimer(context) },

                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("BẮT ĐẦU", color = Color.White)
                        }
                    }
                    TimerStatus.RUNNING -> {
                        Button(
                            onClick = { viewModel.pauseTimer(context) },

                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                        ) {
                            Text("TẠM DỪNG", color = Color.Black)
                        }
                    }
                }

                Button(
                    onClick = { viewModel.resetTimer(context) },

                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("HỦY", color = Color.White)
                }
            }
        }
    }
}