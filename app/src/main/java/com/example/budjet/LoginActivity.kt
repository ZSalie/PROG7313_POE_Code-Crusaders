package com.example.budjet // Keep your original package name here!

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hides the default top bar to keep your custom design clean
        supportActionBar?.hide()
        setContentView(R.layout.activity_login_activtity) // Make sure this matches your XML file name

        // 1. Find all our interactive views by their IDs
        val tvSignupLink = findViewById<TextView>(R.id.tvSignupLink)
        val btnLogin = findViewById<AppCompatButton>(R.id.btnLogin)
        val etName = findViewById<EditText>(R.id.etName)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        // 2. Set up the navigation Intent for the Sign Up link
        tvSignupLink.setOnClickListener {
            // This reads: "From this screen (this), go to MainActivity"
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 3. Set up a basic click for the Login button
        btnLogin.setOnClickListener {
            val name = etName.text.toString()
            if (name.isNotEmpty()) {
                Toast.makeText(this, "Welcome back, $name!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter your name.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}