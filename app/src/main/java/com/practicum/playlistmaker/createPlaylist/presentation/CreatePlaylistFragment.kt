package com.practicum.playlistmaker.createPlaylist.presentation

import android.os.Bundle
import android.os.TokenWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding

class CreatePlaylistFragment : Fragment() {
    private lateinit var binding: FragmentCreatePlaylistBinding
    private lateinit var textWatcher: TextWatcher

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePlaylistBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textWatcher = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val string = s?.toString()
                binding.createButton.isEnabled = string?.isNotEmpty() ?: false
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        textWatcher.let { binding.albumNameEditText.addTextChangedListener(it) }
    }
}