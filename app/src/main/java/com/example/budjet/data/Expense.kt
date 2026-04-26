package com.example.budjet.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(

    @PrimaryKey(autoGenerate = true)
    val expenseId: Int = 0,

    val userId: Int,
    val category: String,

    val amount: Double,
    val description: String,

    val date: String,
    val startTime: String,
    val endTime: String,

    val photoPath: String?
)