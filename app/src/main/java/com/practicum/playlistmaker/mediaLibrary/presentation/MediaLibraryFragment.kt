package com.practicum.playlistmaker.mediaLibrary.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaLibraryBinding

class MediaLibraryFragment : Fragment() {
    private lateinit var binding: FragmentMediaLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MediaLibraryViewPagerAdapter(childFragmentManager, lifecycle)
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
}