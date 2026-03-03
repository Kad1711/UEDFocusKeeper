package com.example.uedfocuskeeper.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import android.content.pm.ServiceInfo
import android.os.Build
class TimerService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var notificationHelper: NotificationHelper
    private var timerJob: Job? = null

    companion object {
        val timeLeftFlow = MutableStateFlow(0)
        val isRunningFlow = MutableStateFlow(false)
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val duration = intent?.getIntExtra("DURATION", 0) ?: 0
        val courseName = intent?.getStringExtra("COURSE_NAME") ?: "Môn học"

        when (action) {
            "START" -> startTimer(duration, courseName)
            "STOP" -> stopTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer(duration: Int, courseName: String) {
        timeLeftFlow.value = duration
        isRunningFlow.value = true

        val notification = notificationHelper.buildNotification("Bắt đầu phiên học: $courseName")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Từ Android 14 trở lên
            startForeground(
                NotificationHelper.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NotificationHelper.NOTIFICATION_ID, notification)
        }

        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (timeLeftFlow.value > 0) {
                delay(1000L)
                timeLeftFlow.value -= 1

                val minutes = timeLeftFlow.value / 60
                val seconds = timeLeftFlow.value % 60
                notificationHelper.updateNotification("Đang tập trung $courseName: ${String.format("%02d:%02d", minutes, seconds)}")
            }
            isRunningFlow.value = false
            notificationHelper.updateNotification("Phiên học hoàn tất: $courseName")
            stopForeground(STOP_FOREGROUND_DETACH)
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        isRunningFlow.value = false
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}