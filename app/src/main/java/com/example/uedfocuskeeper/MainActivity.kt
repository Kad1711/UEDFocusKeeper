package com.example.uedfocuskeeper

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uedfocuskeeper.data.StudyDatabase
import com.example.uedfocuskeeper.ui.screens.HistoryScreen
import com.example.uedfocuskeeper.ui.screens.TimerScreen
import com.example.uedfocuskeeper.ui.theme.UEDFocusKeeperTheme
import com.example.uedfocuskeeper.viewmodel.HistoryViewModel
import com.example.uedfocuskeeper.viewmodel.HistoryViewModelFactory
import com.example.uedfocuskeeper.viewmodel.TimerViewModel
import com.example.uedfocuskeeper.viewmodel.TimerViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        val database = StudyDatabase.getDatabase(this)
        val dao = database.studySessionDao()

        setContent {
            UEDFocusKeeperTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "timer_screen"
                ) {
                    // Định nghĩa màn hình Đếm giờ
                    composable("timer_screen") {
                        val timerViewModel: TimerViewModel = viewModel(
                            factory = TimerViewModelFactory(dao)
                        )
                        TimerScreen(
                            viewModel = timerViewModel,
                            onNavigateToHistory = {
                                navController.navigate("history_screen")
                            }
                        )
                    }

                    composable("history_screen") {
                        val historyViewModel: HistoryViewModel = viewModel(
                            factory = HistoryViewModelFactory(dao)
                        )
                        HistoryScreen(
                            viewModel = historyViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}