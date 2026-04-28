package com.example.budjet

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budjet.data.AppDatabase
import com.example.budjet.data.BudJetDao
import com.example.budjet.data.Expense
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var dao: BudJetDao
    private lateinit var adapter: ExpenseAdapter
    private lateinit var rvExpenses: RecyclerView
    private lateinit var tvTotalAmount: TextView
    private var currentUserId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses_list)

        currentUserId = getSharedPreferences("app_prefs", MODE_PRIVATE).getInt("currentUserId", 1)

        db = AppDatabase.getInstance(this)
        dao = db.budJetDao()

        rvExpenses = findViewById(R.id.rvExpenses)
        tvTotalAmount = findViewById(R.id.tvTotalExpensesAmount)

        adapter = ExpenseAdapter(mutableListOf()) { expense ->

            showDeleteConfirmationDialog(expense)
        }
        rvExpenses.layoutManager = LinearLayoutManager(this)
        rvExpenses.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddExpense).setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("USER_ID", currentUserId)
            startActivity(intent)
        }

        loadExpenses()
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    private fun loadExpenses() {
        lifecycleScope.launch {
            dao.getExpensesByUser(currentUserId).collect { expenses ->
                adapter.updateExpenses(expenses)
                updateTotalAmount(expenses)
            }
        }
    }

    private fun updateTotalAmount(expenses: List<Expense>) {
        val total = expenses.sumOf { it.amount }
        tvTotalAmount.text = String.format("R %,.2f", total)
    }

    private fun showDeleteConfirmationDialog(expense: Expense) {
        AlertDialog.Builder(this)
            .setTitle("Delete Expense")
            .setMessage("Are you sure you want to delete this expense?\n\nCategory: ${expense.category}\nAmount: R ${expense.amount}")
            .setPositiveButton("Delete") { _, _ ->
                deleteExpense(expense)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteExpense(expense: Expense) {
        lifecycleScope.launch {
            try {
                dao.deleteExpense(expense)
                Toast.makeText(this@ExpenseListActivity, "Expense deleted", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Toast.makeText(this@ExpenseListActivity, "Error deleting expense", Toast.LENGTH_SHORT).show()
            }
        }
    }


    inner class ExpenseAdapter(
        private var expenses: MutableList<Expense>,
        private val onLongPress: (Expense) -> Unit
    ) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

        inner class ExpenseViewHolder(itemView: android.view.View) :
            RecyclerView.ViewHolder(itemView) {
            val iconCategory: TextView = itemView.findViewById(R.id.iconCategory)
            val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
            val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
            val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ExpenseViewHolder {
            val view = layoutInflater.inflate(R.layout.item_expense, parent, false)
            return ExpenseViewHolder(view)
        }

        override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
            val expense = expenses[position]
            holder.tvCategory.text = expense.category
            holder.tvDescription.text = expense.description
            holder.tvAmount.text = String.format("R %.2f", expense.amount)

            val bgColor = when (expense.category.lowercase()) {
                "groceries" -> "#6200EA"
                "clothing" -> "#F44336"
                "utilities", "water and electricity" -> "#555555"
                else -> "#096666"
            }
            holder.iconCategory.setBackgroundColor(android.graphics.Color.parseColor(bgColor))
            holder.iconCategory.text = expense.category.take(1).uppercase()


            holder.itemView.setOnLongClickListener {
                onLongPress(expense)
                true
            }
        }

        override fun getItemCount(): Int = expenses.size

        fun updateExpenses(newExpenses: List<Expense>) {
            expenses.clear()
            expenses.addAll(newExpenses)
            notifyDataSetChanged()
        }
    }
}