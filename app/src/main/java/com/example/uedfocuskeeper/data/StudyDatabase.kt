package com.example.uedfocuskeeper.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StudySession::class], version = 1, exportSchema = false)
abstract class StudyDatabase : RoomDatabase() {

    abstract fun studySessionDao(): StudySessionDao

    companion object {
        @Volatile
        private var Instance: StudyDatabase? = null

        fun getDatabase(context: Context): StudyDatabase {

            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    StudyDatabase::class.java,
                    "ued_focus_database"
                )

                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}