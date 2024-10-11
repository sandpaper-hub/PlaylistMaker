package com.practicum.playlistmaker.mediaLibrary.presentation.playlists

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.presentation.model.PlaylistsState
import com.practicum.playlistmaker.util.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private lateinit var binding: FragmentPlaylistsBinding
    private val viewModel by viewModel<PlaylistsViewModel>()
    private lateinit var playlistsAdapter: PlaylistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemDecoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(4f.dpToPx(requireContext()), 8f.dpToPx(requireContext()), 4f.dpToPx(requireContext()), 8f.dpToPx(requireContext()   )) // Задаем отступы в пикселях (left, top, right, bottom)
            }
        }
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment)
        }

        binding.playlistsRecyclerView.addItemDecoration(itemDecoration)
        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        playlistsAdapter = PlaylistsAdapter()
        binding.playlistsRecyclerView.adapter = playlistsAdapter
        viewModel.fillData()
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showEmpty()
            is PlaylistsState.Content -> showContent(state.playlists)
            else -> Unit
        }
    }

    private fun showEmpty() = with(binding) {
        playlistsRecyclerView.visibility = View.GONE
        emptyLibraryImageView.visibility = View.VISIBLE
        emptyFavoriteTextView.visibility = View.VISIBLE
        emptyFavoriteTextView.setText(R.string.noPlaylist)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(playlists: List<Playlist>) = with(binding) {
        emptyFavoriteTextView.visibility = View.GONE
        emptyLibraryImageView.visibility = View.GONE
        playlistsRecyclerView.visibility = View.VISIBLE
        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }
}