package com.practicum.playlistmaker.mediaLibrary.presentation.createPlaylistFragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.presentation.model.CreatePlaylistState
import com.practicum.playlistmaker.util.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : Fragment() {
    private lateinit var binding: FragmentCreatePlaylistBinding
    private val viewModel by viewModel<CreatePlaylistViewModel>()
    private lateinit var textWatcher: TextWatcher
    private lateinit var imagePicker: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var coverUri: Uri? = null

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
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.checkCreateButton(s.toString().isNotEmpty())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        textWatcher.let { binding.albumNameEditText.addTextChangedListener(it) }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена") { _, _ ->

            }
            .setPositiveButton("Завершить") { _, _ ->
                findNavController().navigateUp()
            }

        imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                coverUri = uri
                Glide.with(requireContext())
                    .load(uri)
                    .placeholder(R.drawable.placeholder)
                    .transform(CenterCrop(), RoundedCorners(8f.dpToPx(requireContext())))
                    .into(binding.albumCoverImageView)
                binding.albumCoverImageView.background = null
                binding.addPhotoIconImageView.visibility = View.GONE
            }
        }

        binding.albumCoverImageView.setOnClickListener {
            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.backButtonFragmentCreate.setOnClickListener {
            viewModel.showDialog()
        }

        binding.createButton.setOnClickListener {
            viewModel.savePlaylist(
                coverUri.toString(),
                "${binding.albumNameEditText.text.toString()}.jpg",
                Playlist(
                    0,
                    binding.albumNameEditText.text.toString(),
                    binding.albumDescriptionEditText.text.toString(),
                    coverUri.toString(),
                    "", 0
                )
            )
            Toast.makeText(
                requireContext(),
                "Плейлист ${binding.albumNameEditText.text} создан",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.showDialog()
        }
    }

    private fun render(state: CreatePlaylistState) {
        when (state) {
            is CreatePlaylistState.EnableCreateButton -> {
                enableCreateButton(state.isEnable)
            }

            is CreatePlaylistState.Dialog -> {
                confirmDialog.show()
            }
        }
    }

    private fun enableCreateButton(enable: Boolean) {
        binding.createButton.isEnabled = enable
    }
}