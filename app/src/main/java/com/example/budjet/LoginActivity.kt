package com.example.budjet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login_activtity)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val tvSignupLink = findViewById<TextView>(R.id.tvSignupLink)
        val btnLogin = findViewById<AppCompatButton>(R.id.btnLogin)

        // Firebase standard auth uses email instead of a simple username
        val etEmail = findViewById<EditText>(R.id.etName)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        tvSignupLink.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Authenticate with Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        // Save the Firebase unique ID string to SharedPreferences
                        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("currentUserId", user?.uid)
                            apply()
                        }

                        Toast.makeText(
                            this@LoginActivity,
                            "Welcome back!",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(this@LoginActivity, WalletActivity::class.java))
                        finish() // Removes LoginActivity from the back stack

                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid login details: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}