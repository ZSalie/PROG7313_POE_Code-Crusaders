package com.example.budjet

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.budjet.data.AppDatabase
import com.example.budjet.data.Expense
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddExpenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        val db = AppDatabase.getInstance(this)
        val dao = db.budJetDao()

        val btnSave = findViewById<Button>(R.id.btnSaveExpense)
        val etCategory = findViewById<TextInputEditText>(R.id.etCategory)
        val etAmount = findViewById<TextInputEditText>(R.id.etAmount)
        val etDescription = findViewById<TextInputEditText>(R.id.etDescription)
        val etDate = findViewById<TextInputEditText>(R.id.etDate)
        val etStartTime = findViewById<TextInputEditText>(R.id.etStartTime)
        val etEndTime = findViewById<TextInputEditText>(R.id.etEndTime)

        btnSave.setOnClickListener {
            val category = etCategory.text.toString().trim()
            val amountText = etAmount.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val date = etDate.text.toString().trim()
            val startTime = etStartTime.text.toString().trim()
            val endTime = etEndTime.text.toString().trim()

            if (category.isEmpty() || amountText.isEmpty() || description.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val userId = sharedPref.getInt("currentUserId", 1)

            val expense = Expense(
                expenseId = 0,
                userId = userId,
                category = category,
                amount = amount,
                description = description,
                date = date,
                startTime = startTime,
                endTime = endTime,
                photoPath = null
            )

            lifecycleScope.launch {
                dao.insertExpense(expense)
                Toast.makeText(this@AddExpenseActivity, "Expense saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}