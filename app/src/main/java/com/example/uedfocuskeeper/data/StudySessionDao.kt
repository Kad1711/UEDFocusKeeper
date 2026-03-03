package com.example.uedfocuskeeper.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Insert
    suspend fun insertSession(session: StudySession)
    @Query("SELECT * FROM study_sessions ORDER BY completion_date DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE completion_date >= :sinceDate ORDER BY completion_date ASC")
    fun getSessionsSince(sinceDate: Long): Flow<List<StudySession>>
}