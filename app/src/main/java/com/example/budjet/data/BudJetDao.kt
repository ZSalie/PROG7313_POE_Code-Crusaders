package com.example.budjet.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

data class CategoryCount(
    val category: String,
    val count: Int
)
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
    @Insert
    suspend fun insertGoal(goal: Goal)

    // Get latest goal
    @Query("SELECT * FROM goals WHERE userId = :uid LIMIT 1")
    suspend fun getGoal(uid: Int): Goal?

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getExpensesByUser(userId: Int): Flow<List<Expense>>

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE userId = :userId AND category = :category ORDER BY date DESC")
    fun getExpensesByCategory(userId: Int, category: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE userId = :userId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(userId: Int, startDate: String, endDate: String): Flow<List<Expense>>

    @Query("""
    SELECT category, COUNT(*) as count 
    FROM expenses 
    WHERE userId = :userId AND date >= :startDate AND date <= :endDate 
    GROUP BY category
""")
    fun getCategoryTotalsByDate(userId: Int, startDate: String, endDate: String): Flow<List<CategoryCount>>
}

