package com.practicum.playlistmaker.mediaLibrary.presentation.createPlaylist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.presentation.model.CreatePlaylistState
import com.practicum.playlistmaker.util.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel

open class CreatePlaylistFragment : Fragment() {
    lateinit var binding: FragmentCreatePlaylistBinding
    open val viewModel by viewModel<CreatePlaylistViewModel>()
    val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.checkCreateButton(s.toString().isNotEmpty())
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }
    var imagePicker: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            coverUriString = uri.toString()
            Glide.with(requireContext())
                .load(uri)
                .placeholder(R.drawable.placeholder)
                .transform(CenterCrop(), RoundedCorners(8f.dpToPx(requireContext())))
                .into(binding.albumCoverImageView)
            binding.albumCoverImageView.background = null
        }
    }

    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    var coverUriString: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePlaylistBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        textWatcher.let { binding.albumNameEditText.addTextChangedListener(it) }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.completePlaylistCreation)
            .setMessage(R.string.dataWillLost)
            .setNegativeButton(R.string.cancel) { _, _ ->

            }
            .setPositiveButton(R.string.complete) { _, _ ->
                findNavController().navigateUp()
            }

        binding.albumCoverImageView.setOnClickListener {
            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.backButtonFragmentCreate.setOnClickListener {
            showDialog()
        }

        binding.createButton.setOnClickListener {
            viewModel.savePlaylist(
                coverUriString,
                "${binding.albumNameEditText.text.toString()}_${System.currentTimeMillis()}.jpg",
                Playlist(
                    0,
                    binding.albumNameEditText.text.toString(),
                    binding.albumDescriptionEditText.text.toString(),
                    null,
                    Gson().toJson(ArrayList<String>()), 0
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
            showDialog()
        }
    }

    open fun render(state: CreatePlaylistState) {
        when (state) {
            is CreatePlaylistState.EnableCreateButton -> {
                enableCreateButton(state.isEnable)
            }

            is CreatePlaylistState.Dialog -> {
                confirmDialog.show()
            }
        }
    }

    fun enableCreateButton(enable: Boolean) {
        binding.createButton.isEnabled = enable
    }

    private fun showDialog() {
        val isNameEmpty = binding.albumNameEditText.text.isNullOrEmpty()
        val isDescriptionEmpty = binding.albumDescriptionEditText.text.isNullOrEmpty()
        val isAlbumCoverEmpty = binding.albumCoverImageView.drawable == null
        if (isNameEmpty && isDescriptionEmpty && isAlbumCoverEmpty) {
            findNavController().navigateUp()
        } else {
            viewModel.showDialog()
        }
    }
}