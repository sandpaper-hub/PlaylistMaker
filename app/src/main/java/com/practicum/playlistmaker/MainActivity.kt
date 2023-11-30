package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val searchButtonListener = findViewById<Button>(R.id.search_button).setOnClickListener{
            val searchActivityIntent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(searchActivityIntent)
        }
        val mediaLibraryButtonListener =
            findViewById<Button>(R.id.media_library_button).setOnClickListener {
                val mediaLibraryActivityIntent = Intent(this, MediaLibraryActivity::class.java)
                startActivity(mediaLibraryActivityIntent)
            }
        val settingsButtonListener = findViewById<Button>(R.id.settings_button).setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}