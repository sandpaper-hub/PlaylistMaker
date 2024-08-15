package com.practicum.playlistmaker.createPlaylist.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.TokenWatcher
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.util.dpToPx
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : Fragment() {
    private lateinit var binding: FragmentCreatePlaylistBinding
    private lateinit var textWatcher: TextWatcher
    private lateinit var imagePicker: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var confirmDialog: MaterialAlertDialogBuilder

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

        textWatcher = object : TextWatcher {
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

        imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                saveCoverToPrivateStorage(uri)
                Glide.with(requireContext())
                    .load(uri)
                    .placeholder(R.drawable.placeholder)
                    .transform(CenterCrop(), RoundedCorners(8f.dpToPx(requireContext())))
                    .into(binding.albumCoverImageView)
                binding.albumCoverImageView.background = null
                binding.addPhotoIconImageView.visibility = View.GONE
            }
        }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена") { _, _ ->

            }
            .setPositiveButton("Завершить") { _, _ ->
                findNavController().navigateUp()
            }

        binding.albumCoverImageView.setOnClickListener {
            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.backButtonFragmentCreate.setOnClickListener {
            confirmDialog.show()
        }

        binding.createButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Плейлист ${binding.albumNameEditText.text} создан",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }
    }


    private fun saveCoverToPrivateStorage(uri: Uri) {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "albumCoverFiles"
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "cover_image.jpg")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStram = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStram)
    }
}