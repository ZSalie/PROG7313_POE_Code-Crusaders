package com.example.budjet

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.budjet.data.AppDatabase
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_login_activtity)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "budjet_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        // test db
        lifecycleScope.launch {
            db.budJetDao().insertUser(
                com.example.budjet.data.User(
                    username = "admin",
                    email = "mail@gmail.com",
                    password = "1234"
                )
            )
        }
        val tvSignupLink = findViewById<TextView>(R.id.tvSignupLink)
        val btnLogin = findViewById<AppCompatButton>(R.id.btnLogin)
        val etName = findViewById<EditText>(R.id.etName)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        tvSignupLink.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnLogin.setOnClickListener {

            val username = etName.text.toString()
            val password = etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {

                val user = db.budJetDao().login(username, password)

                runOnUiThread {
                    if (user != null) {

                        Toast.makeText(
                            this@LoginActivity,
                            "Welcome back, $username!",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(this@LoginActivity, WalletActivity::class.java)
                        )

                    } else {

                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid login details",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}