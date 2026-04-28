package com.example.budjet

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
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
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var dao: BudJetDao
    private lateinit var adapter: ExpenseAdapter
    private lateinit var rvExpenses: RecyclerView
    private lateinit var tvTotalAmount: TextView
    private var currentUserId: Int = 0
    private var selectedCategory: String = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses_list)

        currentUserId = getSharedPreferences("app_prefs", MODE_PRIVATE).getInt("currentUserId", 1)

        db = AppDatabase.getInstance(this)
        dao = db.budJetDao()

        rvExpenses = findViewById(R.id.rvExpenses)
        tvTotalAmount = findViewById(R.id.tvTotalExpensesAmount)

        adapter = ExpenseAdapter(mutableListOf())
        rvExpenses.layoutManager = LinearLayoutManager(this)
        rvExpenses.adapter = adapter

//        val btnExpenses: ImageView = findViewById(R.id.btnGoToExpenses)
//
//        btnExpenses.setOnClickListener {
//            // Create an Intent to go from THIS activity to the ExpenseListActivity
//            val intent = Intent(this, ExpenseListActivity::class.java)
//
//            // If your ExpenseListActivity needs to know which user is logged in
//            // (which it does, based on the code you shared earlier), pass the ID here:
//            val currentUserId = getSharedPreferences("app_prefs", MODE_PRIVATE).getInt("currentUserId", 1)
//            intent.putExtra("currentUserId", currentUserId)
//
//            startActivity(intent)
//        }

        findViewById<FloatingActionButton>(R.id.fabAddExpense).setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("USER_ID", currentUserId)
            startActivity(intent)
        }

        val spinnerFilter: Spinner = findViewById(R.id.spinnerCategoryFilter)
        val categories = arrayOf(
            "All",
            "Groceries",
            "Clothing",
            "Utilities",
            "Water and Electricity",
            "Other"
        )
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = arrayAdapter
        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {

                selectedCategory = categories[position]


                loadExpenses()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Default to showing everything if nothing is picked
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
        lifecycleScope.launch {
            val flow = if (selectedCategory == "All") {
                dao.getExpensesByUser(currentUserId)
            } else {
                dao.getExpensesByCategory(currentUserId, selectedCategory)
            }
            flow.collect { expenses ->
                adapter.updateExpenses(expenses)
                updateTotalAmount(expenses)
            }
        }
    }

    fun filterByCategory(category: String) {
        selectedCategory = category
        loadExpenses()
    }

    private fun updateTotalAmount(expenses: List<Expense>) {
        val total = expenses.sumOf { it.amount }
        tvTotalAmount.text = String.format("R %,.2f", total)
    }

    inner class ExpenseAdapter(private var expenses: MutableList<Expense>) :
        RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

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
        }

        override fun getItemCount(): Int = expenses.size

        fun updateExpenses(newExpenses: List<Expense>) {
            expenses.clear()
            expenses.addAll(newExpenses)
            notifyDataSetChanged()
        }
    }
}