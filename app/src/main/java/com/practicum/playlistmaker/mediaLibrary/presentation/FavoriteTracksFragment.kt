package com.practicum.playlistmaker.mediaLibrary.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.practicum.playlistmaker.mediaLibrary.presentation.model.FavoriteTracksState
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoriteTracksFragment : Fragment() {
    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }

    private lateinit var binding: FragmentFavouriteTracksBinding
    private val viewModel by viewModel<FavoriteTracksViewModel> {
        parametersOf(getText(R.string.emptyLibrary))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: FavoriteTracksState) {
        when (state) {
            is FavoriteTracksState.EmptyMediaLibrary -> showEmpty(state.message)
        }
    }

    private fun showEmpty(message: String) = with(binding) {
        newPlaylistButton.visibility = View.GONE
        emptyFavoriteTextView.text = message
    }
}