package com.example.budjet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
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
    fun getExpenses(uid: Int): Flow<List<Expense>>

    // Total spent by user
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :uid")
    suspend fun getTotal(uid: Int): Double?

    // Save goal
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    // Get latest goal
    @Query("SELECT * FROM goals WHERE userId = :uid LIMIT 1")
    suspend fun getGoal(uid: Int): Goal?

    // Expense list screen
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getExpensesByUser(userId: Int): Flow<List<Expense>>

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // NEW: Get monthly goal
    @Query("SELECT * FROM goals WHERE userId = :userId AND month = :month LIMIT 1")
    suspend fun getGoalForMonth(userId: Int, month: String): Goal?

    // NEW: Monthly spending total
    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM expenses
        WHERE userId = :userId
        AND date LIKE :monthPattern
    """)
    suspend fun getTotalSpendingForMonth(
        userId: Int,
        monthPattern: String
    ): Double

    // NEW: Filter by category
    @Query("""
        SELECT * FROM expenses
        WHERE userId = :userId
        AND category = :category
        ORDER BY date DESC
    """)
    fun getExpensesByCategory(
        userId: Int,
        category: String
    ): Flow<List<Expense>>

    // NEW: Filter by date range
    @Query("""
        SELECT * FROM expenses
        WHERE userId = :userId
        AND date BETWEEN :startDate AND :endDate
        ORDER BY date DESC
    """)
    fun getExpensesByDateRange(
        userId: Int,
        startDate: String,
        endDate: String
    ): Flow<List<Expense>>
}