package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val searchButton = findViewById<Button>(R.id.search_button)
        val searchButtonListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Search Button", Toast.LENGTH_SHORT).show()
            }
        }
        searchButton.setOnClickListener(searchButtonListener)
        val mediaLibraryButton =
            findViewById<Button>(R.id.media_library_button).setOnClickListener {
                Toast.makeText(
                    this@MainActivity,
                    "Media Library Button",
                    Toast.LENGTH_SHORT
                ).show()
            }
        val settingsButton = findViewById<Button>(R.id.settings_button)
        val settingsButtonListener = View.OnClickListener {
            Toast.makeText(
                this@MainActivity,
                "Settings Button",
                Toast.LENGTH_SHORT
            ).show()
        }
        settingsButton.setOnClickListener(settingsButtonListener)
    }
}