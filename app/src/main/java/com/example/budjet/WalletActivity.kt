package com.example.budjet

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class WalletActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)



        findViewById<ImageView>(R.id.navWallet).setOnClickListener {

            startActivity(Intent(this, ExpenseListActivity::class.java))
            finish()
        }




    }
}