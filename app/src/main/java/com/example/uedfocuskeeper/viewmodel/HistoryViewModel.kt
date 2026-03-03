package com.example.uedfocuskeeper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uedfocuskeeper.data.StudySessionDao
import java.util.Calendar

class HistoryViewModel(dao: StudySessionDao) : ViewModel() {

    val allSessions = dao.getAllSessions()

    val last7DaysSessions = dao.getSessionsSince(getSevenDaysAgoTimestamp())

    private fun getSevenDaysAgoTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.timeInMillis
    }
}

class HistoryViewModelFactory(private val dao: StudySessionDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}