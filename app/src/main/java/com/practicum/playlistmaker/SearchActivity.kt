package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<ImageButton>(R.id.back_button_searchActivity)
        backButton.setOnClickListener() {
            val backIntent = Intent(this, MainActivity::class.java)
            backIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            backIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(backIntent)
        }
    }
}