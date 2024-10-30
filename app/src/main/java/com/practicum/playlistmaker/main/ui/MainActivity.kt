package com.practicum.playlistmaker.main.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playerFragment -> hideBottomNavigation()
                R.id.createPlaylistFragment -> hideBottomNavigation()
                R.id.playlistFragment -> hideBottomNavigation()
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.bottomNavigationSeparator.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun hideBottomNavigation() = with(binding) {
        bottomNavigationView.visibility = View.GONE
        bottomNavigationSeparator.visibility = View.GONE
    }
}