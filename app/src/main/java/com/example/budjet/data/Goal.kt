package com.example.budjet.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(

    @PrimaryKey(autoGenerate = true)
    val goalId: Int = 0,

    val userId: Int,
    val month: String,

    val minGoal: Double,
    val maxGoal: Double
)