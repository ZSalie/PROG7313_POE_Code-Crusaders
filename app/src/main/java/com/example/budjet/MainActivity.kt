package com.example.budjet  // <-- make sure this matches your app package

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.budjet.R  // explicitly import your generated R

class MainActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var etDob: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // make sure your XML is activity_main.xml

        // Initialize views
        btnBack = findViewById(R.id.btnBack)
        etDob = findViewById(R.id.etDob)

        // Back button closes the activity
        btnBack.setOnClickListener {
            finish()
        }

        // DOB click - placeholder action
        etDob.setOnClickListener {
            Toast.makeText(this, "Date picker goes here", Toast.LENGTH_SHORT).show()
        }
    }
}