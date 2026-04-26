package com.example.budjet.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BudJetDao {

    // Register user
    @Insert
    suspend fun insertUser(user: User)

    // Login
    @Query("SELECT * FROM users WHERE username = :u AND password = :p LIMIT 1")
    suspend fun login(u: String, p: String): User?

    // Add expense
    @Insert
    suspend fun insertExpense(expense: Expense)

    // Get all expenses for one user
    @Query("SELECT * FROM expenses WHERE userId = :uid")
    suspend fun getExpenses(uid: Int): List<Expense>

    // Total spent by user
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :uid")
    suspend fun getTotal(uid: Int): Double?

    // Save goal
    @Insert
    suspend fun insertGoal(goal: Goal)

    // Get latest goal
    @Query("SELECT * FROM goals WHERE userId = :uid LIMIT 1")
    suspend fun getGoal(uid: Int): Goal?
}