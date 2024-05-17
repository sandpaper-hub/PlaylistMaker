package com.practicum.playlistmaker.mediaLibrary.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaLibraryBinding

class MediaLibraryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButtonMediaLibraryActivity.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val adapter = MediaLibraryViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.mediaLibraryViewPager2.adapter = adapter
        tabMediator = TabLayoutMediator(
            binding.mediaLibraryTabLayout,
            binding.mediaLibraryViewPager2
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getText(R.string.favoriteTracks)
                }

                1 -> tab.text = getText(R.string.playlists)
            }
        }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}