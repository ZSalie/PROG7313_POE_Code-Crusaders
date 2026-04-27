package com.example.budjet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class,
        Expense::class,
        Goal::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun budJetDao(): BudJetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budjet.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}