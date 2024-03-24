package com.practicum.playlistmaker.presentation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.presentation.ui.search.SearchActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchButton.setOnClickListener{
            val searchActivityIntent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(searchActivityIntent)
        }
        binding.mediaLibraryButton.setOnClickListener {
                val mediaLibraryActivityIntent = Intent(this, MediaLibraryActivity::class.java)
                startActivity(mediaLibraryActivityIntent)
            }
        binding.settingsButton.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}