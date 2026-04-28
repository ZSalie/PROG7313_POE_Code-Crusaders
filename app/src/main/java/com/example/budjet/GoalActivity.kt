package com.example.budjet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.budjet.data.AppDatabase
import com.example.budjet.data.Goal
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
class GoalActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var etMinGoal: EditText
    private lateinit var etMaxGoal: EditText
    private lateinit var btnSaveGoal: Button
    private lateinit var tvGoalStatus: TextView
    private lateinit var progressGoal: ProgressBar
    private lateinit var tvCurrentSpending: TextView
    private val userId = 1
    private val currentYearMonth: String
        get() = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    private val monthPattern: String
        get() = "$currentYearMonth%"
    private val monthDisplay: String
        get() = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)
        db = AppDatabase.getInstance(this)
        etMinGoal = findViewById(R.id.etMinGoal)
        etMaxGoal = findViewById(R.id.etMaxGoal)
        btnSaveGoal = findViewById(R.id.btnSaveGoal)
        tvGoalStatus = findViewById(R.id.tvGoalStatus)
        progressGoal = findViewById(R.id.progressGoal)
        tvCurrentSpending = findViewById(R.id.tvCurrentSpending)
        findViewById<ImageView>(R.id.navHome).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.navWallet).setOnClickListener { }
        findViewById<ImageView>(R.id.navEdit).setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
        findViewById<ImageView>(R.id.navProfile).setOnClickListener { }
        loadData()
        btnSaveGoal.setOnClickListener { saveGoal() }
    }
    private fun loadData() {
        lifecycleScope.launch {
            val goal = db.budJetDao().getGoalForMonth(userId, monthDisplay)
            val totalSpent = db.budJetDao().getTotalSpendingForMonth(userId, monthPattern)
            val formatter = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
            tvCurrentSpending.text = formatter.format(totalSpent)
            if (goal != null) {
                etMinGoal.setText(goal.minGoal.toString())
                etMaxGoal.setText(goal.maxGoal.toString())
                updateGoalStatus(goal.minGoal, goal.maxGoal, totalSpent)
            } else {
                updateGoalStatus(null, null, totalSpent)
            }
        }
    }
    private fun updateGoalStatus(
        minGoal: Double?,
        maxGoal: Double?,
        totalSpent: Double
    ) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

        when {
            minGoal == null || maxGoal == null -> {
                tvGoalStatus.text =
                    "No goals set for $monthDisplay.\n\nSet a minimum and maximum monthly spending goal."

                progressGoal.max = 100
                progressGoal.progress = 0
            }

            totalSpent < minGoal -> {
                val deficit = minGoal - totalSpent

                tvGoalStatus.text =
                    "Goal saved for $monthDisplay\n\n" +
                            "Current spending: ${formatter.format(totalSpent)}\n" +
                            "Minimum goal: ${formatter.format(minGoal)}\n" +
                            "Maximum goal: ${formatter.format(maxGoal)}\n\n" +
                            "Status: Below minimum goal.\n" +
                            "You need ${formatter.format(deficit)} more to reach your minimum goal."

                progressGoal.max = 100
                progressGoal.progress =
                    ((totalSpent / minGoal) * 100).toInt().coerceIn(0, 100)
            }

            totalSpent > maxGoal -> {
                val overshoot = totalSpent - maxGoal

                tvGoalStatus.text =
                    "Goal saved for $monthDisplay\n\n" +
                            "Current spending: ${formatter.format(totalSpent)}\n" +
                            "Minimum goal: ${formatter.format(minGoal)}\n" +
                            "Maximum goal: ${formatter.format(maxGoal)}\n\n" +
                            "Status: Over maximum goal.\n" +
                            "You exceeded your maximum by ${formatter.format(overshoot)}."

                progressGoal.max = 100
                progressGoal.progress = 100
            }

            else -> {
                val percent =
                    ((totalSpent - minGoal) / (maxGoal - minGoal) * 100)
                        .toInt()
                        .coerceIn(0, 100)

                tvGoalStatus.text =
                    "Goal saved for $monthDisplay\n\n" +
                            "Current spending: ${formatter.format(totalSpent)}\n" +
                            "Minimum goal: ${formatter.format(minGoal)}\n" +
                            "Maximum goal: ${formatter.format(maxGoal)}\n\n" +
                            "Status: Within goal range.\n" +
                            "You are staying within your monthly spending goal."

                progressGoal.max = 100
                progressGoal.progress = percent
            }
        }
    }
    private fun saveGoal() {
        val minText = etMinGoal.text.toString().trim()
        val maxText = etMaxGoal.text.toString().trim()

        if (minText.isEmpty() || maxText.isEmpty()) {
            Toast.makeText(this, "Please enter both min and max goal", Toast.LENGTH_SHORT).show()
            return
        }

        val min = minText.toDoubleOrNull()
        val max = maxText.toDoubleOrNull()

        if (min == null || max == null) {
            Toast.makeText(this, "Invalid numbers", Toast.LENGTH_SHORT).show()
            return
        }

        if (min < 0 || max < 0) {
            Toast.makeText(this, "Goals cannot be negative", Toast.LENGTH_SHORT).show()
            return
        }

        if (min >= max) {
            Toast.makeText(this, "Minimum must be less than maximum", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val existingGoal = db.budJetDao().getGoalForMonth(userId, monthDisplay)

            val goal = Goal(
                goalId = existingGoal?.goalId ?: 0,
                userId = userId,
                month = monthDisplay,
                minGoal = min,
                maxGoal = max
            )

            db.budJetDao().insertGoal(goal)

            Toast.makeText(
                this@GoalActivity,
                "Goal saved for $monthDisplay",
                Toast.LENGTH_SHORT
            ).show()

            val totalSpent = db.budJetDao().getTotalSpendingForMonth(userId, monthPattern)

            val formatter = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
            tvCurrentSpending.text = formatter.format(totalSpent)

            updateGoalStatus(min, max, totalSpent)
        }
    }
}
