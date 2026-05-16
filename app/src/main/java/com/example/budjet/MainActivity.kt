package com.example.budjet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.example.budjet.data.BudgetRepository
import com.example.budjet.data.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvLoginLink: TextView

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignUp: AppCompatButton

    private lateinit var auth: FirebaseAuth
    private lateinit var repository: BudgetRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        repository = BudgetRepository()

        btnBack = findViewById(R.id.btnBack)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnBack.setOnClickListener {
            finish()
        }

        tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnSignUp.setOnClickListener {

            val username = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        val uid = firebaseUser?.uid ?: ""

                        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("currentUserId", uid)
                            apply()
                        }

                        val newUser = User(
                            userId = uid,
                            username = username,
                            email = email
                        )

                        lifecycleScope.launch {
                            repository.saveNewUser(newUser)

                            Toast.makeText(
                                this@MainActivity,
                                "Account created!",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(this@MainActivity, WalletActivity::class.java)
                            )
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Sign up failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}