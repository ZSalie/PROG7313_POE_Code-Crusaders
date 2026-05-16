package com.example.budjet.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BudgetRepository {

    private val db = FirebaseFirestore.getInstance()

    private val usersCollection = db.collection("users")
    private val expensesCollection = db.collection("expenses")
    private val goalsCollection = db.collection("goals")

    suspend fun saveNewUser(user: User) {
        usersCollection.document(user.userId).set(user).await()
    }

    suspend fun insertExpense(expense: Expense) {
        expensesCollection.add(expense).await()
    }

    suspend fun insertGoal(goal: Goal) {
        // Creates a unique ID like "user123_2026-05"
        val customDocumentId = "${goal.userId}_${goal.month}"
        goalsCollection.document(customDocumentId).set(goal).await()
    }

    fun getExpensesByUser(userId: String): Flow<List<Expense>> = callbackFlow {
        val subscription = expensesCollection
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val expenses = snapshot.toObjects(Expense::class.java)
                    trySend(expenses)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun deleteExpense(expenseId: String) {
        expensesCollection.document(expenseId).delete().await()

    }
    suspend fun getGoalForMonth(userId: String, month: String): Goal? {
        val customDocumentId = "${userId}_${month}"
        val snapshot = goalsCollection.document(customDocumentId).get().await()
        return snapshot.toObject(Goal::class.java)
    }

    suspend fun getTotalSpendingForMonth(userId: String, yearMonth: String): Double {
        val snapshot = expensesCollection
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("date", "$yearMonth-01")
            .whereLessThanOrEqualTo("date", "$yearMonth-31")
            .get().await()
        return snapshot.toObjects(Expense::class.java).sumOf { it.amount }
    }
}