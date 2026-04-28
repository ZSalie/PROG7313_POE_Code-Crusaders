package com.example.budjet

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlinx.coroutines.Job
class ExpenseListActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var dao: BudJetDao
    private lateinit var adapter: ExpenseAdapter
    private lateinit var rvExpenses: RecyclerView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvCategoryTotals: TextView
    private var currentUserId: Int = 0
    private var selectedCategory: String = "All"
    private var startDateStr: String? = null
    private var endDateStr: String? = null
    private var expensesJob: Job? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses_list)

        findViewById<ImageView>(R.id.navHome).setOnClickListener {

            startActivity(Intent(this, WalletActivity::class.java))
            finish()
        }

        currentUserId = getSharedPreferences("app_prefs", MODE_PRIVATE).getInt("currentUserId", 1)

        db = AppDatabase.getInstance(this)
        dao = db.budJetDao()

        rvExpenses = findViewById(R.id.rvExpenses)
        tvTotalAmount = findViewById(R.id.tvTotalExpensesAmount)
        tvCategoryTotals = findViewById(R.id.tvCategoryTotals)

        adapter = ExpenseAdapter(mutableListOf()) { expense ->
            showDeleteConfirmationDialog(expense)
        }
        rvExpenses.layoutManager = LinearLayoutManager(this)
        rvExpenses.adapter = adapter

        findViewById<Button>(R.id.btnStartDate).setOnClickListener { showDatePicker(true) }
        findViewById<Button>(R.id.btnEndDate).setOnClickListener { showDatePicker(false) }

        findViewById<ImageButton>(R.id.btnClearDate).setOnClickListener {
            startDateStr = null
            endDateStr = null
            findViewById<Button>(R.id.btnStartDate).text = "Start Date"
            findViewById<Button>(R.id.btnEndDate).text = "End Date"
            tvCategoryTotals.text = ""
            loadExpenses()
        }

        findViewById<FloatingActionButton>(R.id.fabAddExpense).setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("USER_ID", currentUserId)
            startActivity(intent)
        }

        val spinnerFilter: Spinner = findViewById(R.id.spinnerCategoryFilter)
        val categories = listOf("All", "Groceries", "Clothing", "Utilities", "Water and Electricity", "Other")
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = arrayAdapter
        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedCategory = categories[position]
                loadExpenses()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCategory = "All"
                loadExpenses()
            }
        }

        loadExpenses()
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }


    private fun loadExpenses() {
        expensesJob?.cancel()

        expensesJob = lifecycleScope.launch {
            val flow: Flow<List<Expense>> = when {
                startDateStr != null && endDateStr != null -> {
                    loadCategoryTotals()
                    dao.getExpensesByDateRange(currentUserId, startDateStr!!, endDateStr!!)
                }

                selectedCategory != "All" -> {
                    tvCategoryTotals.text = ""
                    dao.getExpensesByCategory(currentUserId, selectedCategory)
                }

                else -> {
                    tvCategoryTotals.text = ""
                    dao.getExpensesByUser(currentUserId)
                }
            }

            flow.collect { expenses ->
                adapter.updateExpenses(expenses)
                updateTotalAmount(expenses)

                Toast.makeText(
                    this@ExpenseListActivity,
                    "Loaded ${expenses.size} expenses",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateTotalAmount(expenses: List<Expense>) {
        val total = expenses.sumOf { it.amount }
        tvTotalAmount.text = String.format("R %,.2f", total)
    }

    private fun loadCategoryTotals() {
        if (startDateStr == null || endDateStr == null) return
        lifecycleScope.launch {
            dao.getCategoryTotalsByDate(currentUserId, startDateStr!!, endDateStr!!).collect { totals ->
                displayTotals(totals)
            }
        }
    }

    private fun displayTotals(totals: List<com.example.budjet.data.CategoryCount>) {
        if (totals.isEmpty()) {
            tvCategoryTotals.text = "No expenses found for these dates."
            return
        }
        val sb = StringBuilder("--- Summary for Period ---\n")
        for (item in totals) {
            sb.append("• ${item.category}: ${item.count} items\n")
        }
        tvCategoryTotals.text = sb.toString()
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        android.app.DatePickerDialog(this, { _, year, month, day ->
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            if (isStartDate) {
                startDateStr = formattedDate
                findViewById<Button>(R.id.btnStartDate).text = formattedDate
            } else {
                endDateStr = formattedDate
                findViewById<Button>(R.id.btnEndDate).text = formattedDate
            }
            if (startDateStr != null && endDateStr != null) {
                loadExpenses()
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
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
    fun filterByCategory(category: String) {
        selectedCategory = category
        loadExpenses()
    }

    inner class ExpenseAdapter(
        private var expenses: MutableList<Expense>,
        private val onLongPress: (Expense) -> Unit
    ) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

        inner class ExpenseViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
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