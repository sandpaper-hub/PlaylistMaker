package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.SharedPreferencesData
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.hasNullableData
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.TracksState
import com.practicum.playlistmaker.search.presentation.TracksSearchPresenter
import com.practicum.playlistmaker.search.presentation.TracksView

class SearchActivity : AppCompatActivity(), TracksView {

    companion object {
        const val INSTANCE_STATE_KEY = "SAVED_RESULT"
        const val INTENT_EXTRA_KEY = "selectedTrack"
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var binding: ActivitySearchBinding

    var savedText = ""
    private var restoredText = ""
    private var lastSearchText: String = ""

    private lateinit var historyArray: ArrayList<Track>
    private var isClickAllowed = true

    private lateinit var trackListAdapter: TrackListAdapter
    private lateinit var historyAdapter: TrackListAdapter

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var mainHandler: Handler
    private var textWatcher: TextWatcher? = null
    private val tracksSearchPresenter: TracksSearchPresenter = Creator.provideTracksSearchPresenter(this, this)
    private val getHistoryUseCase = Creator.getGetHistoryUseCase()

    @SuppressLint("NotifyDataSetChanged")
    private val sharedPreferencesChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == SharedPreferencesData.NEW_HISTORY_ITEM_KEY) {
                historyAdapter.trackList = getHistoryUseCase.execute()
                historyAdapter.notifyDataSetChanged()
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainHandler = Handler(Looper.getMainLooper())
        val addTrackToHistoryUseCaseImpl = Creator.getAddTrackToHistoryUseCase()
        val clearHistoryUseCaseImpl = Creator.getClearHistoryUseCase()

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.historySearchContainer.isVisible =
                    (binding.searchEditText.hasFocus() && s?.isEmpty() == true) && historyAdapter.trackList.isNotEmpty() == true
                lastSearchText = tracksSearchPresenter.searchDebounce(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
                savedText = binding.searchEditText.text.toString()
            }
        }

        textWatcher.let { binding.searchEditText.addTextChangedListener(it) }

        sharedPreferences =
            getSharedPreferences(
                SharedPreferencesData.SHARED_PREFERENCES_HISTORY_FILE,
                MODE_PRIVATE
            )

        historyArray = getHistoryUseCase.execute()

        binding.historySearchContainer.isVisible = historyArray.isNotEmpty()

        historyAdapter =
            TrackListAdapter(object : TrackListAdapter.OnTrackClickListener {
                override fun onItemClick(track: Track) {
                    if (clickDebounce()) {
                        val playerIntent = Intent(applicationContext, PlayerActivity::class.java)
                        playerIntent.putExtra(INTENT_EXTRA_KEY, track)
                        startActivity(playerIntent)
                    }
                }
            })
        historyAdapter.trackList = historyArray

        binding.historyRecycler.adapter = historyAdapter

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesChangeListener)

        binding.clearHistory.setOnClickListener {
            clearHistoryUseCaseImpl.execute()
            binding.historySearchContainer.visibility = View.GONE
        }

        trackListAdapter =
            TrackListAdapter(object : TrackListAdapter.OnTrackClickListener {
                override fun onItemClick(track: Track) {
                    if (!track.hasNullableData()) {
                        if (clickDebounce()) {
                            addTrackToHistoryUseCaseImpl.execute(track)
                            val playerIntent =
                                Intent(applicationContext, PlayerActivity::class.java)
                            playerIntent.putExtra(
                                INTENT_EXTRA_KEY,
                                track
                            )
                            startActivity(playerIntent)
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Track has empty data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })

        binding.trackListRecyclerView.adapter = trackListAdapter

        binding.backButtonSearchActivity.setOnClickListener {//ok
            onBackPressedDispatcher.onBackPressed()
        }

        binding.searchEditText.isSaveEnabled = false
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->  //ok
            tracksSearchPresenter.showHideClearEditTextButton(text.toString())
        }

        binding.searchEditText.setOnEditorActionListener { editTextView, actionId, _ -> //ok
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (editTextView.text.isNotEmpty()) {
                    lastSearchText =
                        tracksSearchPresenter.searchDebounce(editTextView.text.toString())
                }
            }
            false
        }

        binding.clearSearchEdiText.setOnClickListener {//ok
            binding.searchEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.refreshSearchButton.setOnClickListener {//ok
            lastSearchText = tracksSearchPresenter.searchDebounce(lastSearchText)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) { //остаётся
        super.onRestoreInstanceState(savedInstanceState)
        restoredText = savedInstanceState.getString(INSTANCE_STATE_KEY).toString()
        binding.searchEditText.setText(restoredText)
    }

    override fun onSaveInstanceState(outState: Bundle) { //остаётся
        super.onSaveInstanceState(outState)
        outState.putString(INSTANCE_STATE_KEY, savedText)
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let { binding.searchEditText.removeTextChangedListener(it) }
        tracksSearchPresenter.onDestroy()
    }

    private fun clickDebounce(): Boolean { //остаётся
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun render(state: TracksState) { //ok
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.ConnectionError -> showConnectionError(
                state.errorMessage,
                state.drawable
            )

            is TracksState.NothingFound -> showEmpty(state.message, state.drawable)
            is TracksState.ClearedEditText -> showHideClearEditTextButton(state.text)
//            is TracksState.HistoryContent ->
        }
    }

    private fun showLoading() { //ok
        binding.searchProgressBar.visibility = View.VISIBLE

        binding.badSearchResultGroup.visibility = View.GONE
        binding.historySearchContainer.visibility = View.GONE
        binding.trackListRecyclerView.visibility = View.GONE
        binding.connectionErrorGroup.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(tracks: List<Track>) { //ok
        binding.trackListRecyclerView.visibility = View.VISIBLE

        binding.searchProgressBar.visibility = View.GONE
        binding.historySearchContainer.visibility = View.GONE
        binding.connectionErrorGroup.visibility = View.GONE

        trackListAdapter.trackList.clear()
        trackListAdapter.trackList.addAll(tracks)
        trackListAdapter.notifyDataSetChanged()
    }

    private fun showConnectionError(errorMessage: String, drawable: Drawable?) { //ok
        binding.connectionErrorGroup.visibility = View.VISIBLE
        binding.badSearchResultText.text = errorMessage
        binding.badSearchResultImage.setImageDrawable(drawable)

        binding.trackListRecyclerView.visibility = View.GONE
        binding.searchProgressBar.visibility = View.GONE
        binding.historySearchContainer.visibility = View.GONE
    }

    private fun showEmpty(errorMessage: String, drawable: Drawable?) { //ok
        binding.badSearchResultText.text = errorMessage
        binding.badSearchResultImage.setImageDrawable(drawable)
        binding.badSearchResultGroup.visibility = View.VISIBLE
        Log.d("RENDERING_STATE", binding.badSearchResultImage.visibility.toString())

        binding.trackListRecyclerView.visibility = View.GONE
        binding.searchProgressBar.visibility = View.GONE
        binding.historySearchContainer.visibility = View.GONE
    }

    private fun showHideClearEditTextButton(text: String) {
        if (text.isEmpty()) {
            binding.clearSearchEdiText.visibility = View.GONE
            binding.trackListRecyclerView.visibility = View.GONE
        } else {
            binding.clearSearchEdiText.visibility = View.VISIBLE
        }
    }
}
