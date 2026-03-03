package com.example.uedfocuskeeper.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.uedfocuskeeper.data.StudySession
import com.example.uedfocuskeeper.data.StudySessionDao
import com.example.uedfocuskeeper.service.TimerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat
enum class TimerStatus { IDLE, RUNNING, PAUSED }

class TimerViewModel(private val studySessionDao: StudySessionDao) : ViewModel() {

    private val _courseName = MutableStateFlow("")
    val courseName: StateFlow<String> = _courseName.asStateFlow()

    private val _totalTimeInSeconds = MutableStateFlow(25 * 60)
    val totalTimeInSeconds: StateFlow<Int> = _totalTimeInSeconds.asStateFlow()

    private val _timeLeftInSeconds = MutableStateFlow(25 * 60)
    val timeLeftInSeconds: StateFlow<Int> = _timeLeftInSeconds.asStateFlow()

    private val _timerStatus = MutableStateFlow(TimerStatus.IDLE)
    val timerStatus: StateFlow<TimerStatus> = _timerStatus.asStateFlow()

    init {
        // Lắng nghe luồng đếm ngược từ TimerService để cập nhật vòng tròn Canvas
        viewModelScope.launch {
            TimerService.timeLeftFlow.collect { time ->
                if (TimerService.isRunningFlow.value) {
                    _timeLeftInSeconds.value = time

                    // Nếu đếm về 0 thì tự động gọi hàm hoàn thành phiên học
                    if (time == 0 && _timerStatus.value == TimerStatus.RUNNING) {
                        finishSession()
                    }
                }
            }
        }
    }

    fun updateCourseName(name: String) {
        _courseName.value = name
    }

    fun updateTotalTime(minutes: Int) {
        val seconds = minutes * 60
        _totalTimeInSeconds.value = seconds
        if (_timerStatus.value == TimerStatus.IDLE) {
            _timeLeftInSeconds.value = seconds
        }
    }

    fun startTimer(context: Context) {
        if (_courseName.value.isBlank()) return

        _timerStatus.value = TimerStatus.RUNNING

        val intent = Intent(context, TimerService::class.java).apply {
            action = "START"
            putExtra("DURATION", _timeLeftInSeconds.value)
            putExtra("COURSE_NAME", _courseName.value)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun pauseTimer(context: Context) {
        _timerStatus.value = TimerStatus.PAUSED

        val intent = Intent(context, TimerService::class.java).apply {
            action = "STOP"
        }
        context.startService(intent)
    }

    fun resetTimer(context: Context) {
        _timerStatus.value = TimerStatus.IDLE
        _timeLeftInSeconds.value = _totalTimeInSeconds.value

        val intent = Intent(context, TimerService::class.java).apply {
            action = "STOP"
        }
        context.startService(intent)
    }

    private fun finishSession() {
        _timerStatus.value = TimerStatus.IDLE
        _timeLeftInSeconds.value = _totalTimeInSeconds.value

        viewModelScope.launch {
            val session = StudySession(
                courseName = _courseName.value,
                durationMinutes = _totalTimeInSeconds.value / 60,
                completionDate = System.currentTimeMillis()
            )
            studySessionDao.insertSession(session)
        }
    }
}

class TimerViewModelFactory(private val dao: StudySessionDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}