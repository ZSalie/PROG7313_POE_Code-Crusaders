package com.example.budjet.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class,
        Expense::class,
        Goal::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun budJetDao(): BudJetDao
}