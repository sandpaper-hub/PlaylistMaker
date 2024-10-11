package com.practicum.playlistmaker.mediaLibrary.presentation.favoriteTracks

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.mediaLibrary.presentation.model.FavoriteTracksState
import com.practicum.playlistmaker.player.presentation.PlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.presentation.TrackListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {
    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }

    private lateinit var binding: FragmentFavoriteTracksBinding
    private lateinit var adapter: TrackListAdapter
    private val viewModel by viewModel<FavoriteTracksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        adapter = TrackListAdapter(object : TrackListAdapter.OnTrackClickListener {
            override fun onItemClick(track: Track) {
                findNavController().navigate(
                    R.id.action_mediaLibraryFragment_to_playerFragment,
                    PlayerFragment.createArgs(track)
                )
            }
        })
        binding.favoriteRecyclerView.adapter = adapter

        viewModel.fillData()
    }

    private fun render(state: FavoriteTracksState) {
        when (state) {
            is FavoriteTracksState.EmptyMediaLibrary -> showEmpty()
            is FavoriteTracksState.Content -> showContent(state.tracks)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(tracks: List<Track>) = with(binding) {
        emptyFavoriteGroup.visibility = View.GONE
        favoriteRecyclerView.visibility = View.VISIBLE

        adapter.trackList.clear()
        adapter.trackList.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showEmpty() = with(binding) {
        emptyFavoriteGroup.visibility = View.VISIBLE
        favoriteRecyclerView.visibility = View.GONE

        adapter.trackList.clear()
        adapter.notifyDataSetChanged()
    }
}