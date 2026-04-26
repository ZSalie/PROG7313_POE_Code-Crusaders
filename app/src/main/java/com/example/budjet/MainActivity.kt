package com.example.budjet

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.budjet.data.AppDatabase
import com.example.budjet.data.User
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvSubtitle: TextView

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignUp: AppCompatButton

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "budjet_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        btnBack = findViewById(R.id.btnBack)
        tvSubtitle = findViewById(R.id.tvSubtitle)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnBack.setOnClickListener {
            finish()
        }

        tvSubtitle.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, LoginActivity::class.java)
            )
        }

        btnSignUp.setOnClickListener {

            val username = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {

                db.budJetDao().insertUser(
                    User(
                        username = username,
                        email = email,
                        password = password
                    )
                )

                runOnUiThread {

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
            }
        }
    }
}