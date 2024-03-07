package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val INSTANCE_STATE_KEY = "SAVED_RESULT"
        const val INTENT_EXTRA_KEY = "selectedTrack"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val RESPONSE_STATUS_SUCCESS = 0
        const val RESPONSE_STATUS_NOTHING_FOUND = 1
        const val RESPONSE_STATUS_BAD_CONNECTION = 2
    }

    private lateinit var binding: ActivitySearchBinding

    var savedText = ""
    private var restoredText = ""

    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesSearchService = retrofit.create(ITunesSearchApi::class.java)

    private val trackList: ArrayList<Track> = ArrayList()
    private lateinit var historyArray: ArrayList<Track>
    private var isClickAllowed = true

    lateinit var trackListAdapter: TrackListAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private val searchRunnable = Runnable { doRequest() }
    private lateinit var mainHandler: Handler

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainHandler = Handler(Looper.getMainLooper())

        sharedPreferences =
            getSharedPreferences(SharedPreferencesData.SHARED_PREFERENCES_HISTORY_FILE, MODE_PRIVATE)
        val historyPreferences = HistoryPreferences(sharedPreferences)

        val json = sharedPreferences.getString(SharedPreferencesData.NEW_HISTORY_ITEM_KEY, null)
        historyArray = json?.createArrayListFromJson() ?: ArrayList()

        val historyAdapter =
            TrackListAdapter(historyArray, object : TrackListAdapter.OnTrackClickListener {
                override fun onItemClick(track: Track) {
                    if (clickDebounce()) {
                        val playerIntent = Intent(applicationContext, PlayerActivity::class.java)
                        playerIntent.putExtra(INTENT_EXTRA_KEY, track)
                        startActivity(playerIntent)
                    }
                }
            })

        binding.historyRecycler.adapter = historyAdapter

        sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == SharedPreferencesData.NEW_HISTORY_ITEM_KEY) {
                val jsonArray =
                    sharedPreferences.getString(SharedPreferencesData.NEW_HISTORY_ITEM_KEY, null)
                if (jsonArray != null) {
                    historyAdapter.trackList = jsonArray.createArrayListFromJson()
                }
                historyAdapter.notifyDataSetChanged()
            }
        }

        binding.clearHistory.setOnClickListener {
            historyPreferences.clearData()
            binding.historySearchContainer.visibility = View.GONE
        }

        trackListAdapter =
            TrackListAdapter(trackList, object : TrackListAdapter.OnTrackClickListener {
                override fun onItemClick(track: Track) {
                    if (clickDebounce()) {
                        historyPreferences.addTrack(historyArray, track)
                        val playerIntent = Intent(applicationContext, PlayerActivity::class.java)
                        playerIntent.putExtra(
                            INTENT_EXTRA_KEY,
                            track
                        )
                        startActivity(playerIntent)
                    }
                }
            })

        binding.trackListRecyclerView.adapter = trackListAdapter

        binding.backButtonSearchActivity.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.searchEditText.isSaveEnabled = false
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                binding.clearSearchEdiText.visibility = View.GONE
                binding.trackListRecyclerView.visibility = View.GONE
            } else {
                binding.clearSearchEdiText.visibility = View.VISIBLE
                binding.trackListRecyclerView.visibility = View.VISIBLE
            }
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            binding.historySearchContainer.isVisible =
                hasFocus && binding.searchEditText.text.isEmpty() && historyArray.isNotEmpty()

        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.historySearchContainer.isVisible =
                    (binding.searchEditText.hasFocus() && s?.isEmpty() == true) && historyAdapter.trackList.isNotEmpty() == true
                binding.connectionErrorGroup.visibility = View.GONE
                binding.trackListRecyclerView.visibility = View.GONE
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                savedText = binding.searchEditText.text.toString()
            }
        })

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doRequest()
            }
            false
        }

        binding.clearSearchEdiText.setOnClickListener {
            binding.searchEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
            showResult(RESPONSE_STATUS_SUCCESS)
            setDataChanged()
        }

        binding.refreshSearchButton.setOnClickListener {
            doRequest()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoredText = savedInstanceState.getString(INSTANCE_STATE_KEY).toString()
        val searchEditText = findViewById<EditText>(R.id.search_editText)
        searchEditText.setText(restoredText)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INSTANCE_STATE_KEY, savedText)
    }

    private fun doRequest() {
        binding.searchProgressBar.visibility = View.VISIBLE
        iTunesSearchService.search(binding.searchEditText.text.toString()).enqueue(object :
            Callback<TrackResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {
                binding.searchProgressBar.visibility = View.GONE
                if (response.code() == 200) {
                    trackList.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        trackList.addAll(response.body()?.results!!)
                        trackListAdapter.notifyDataSetChanged()
                        binding.trackListRecyclerView.visibility = View.VISIBLE
                    }
                    if (trackList.isEmpty()) {
                        showResult(RESPONSE_STATUS_NOTHING_FOUND)
                    } else {
                        showResult(RESPONSE_STATUS_SUCCESS)
                    }
                } else {
                    showResult(RESPONSE_STATUS_BAD_CONNECTION)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                binding.searchProgressBar.visibility = View.GONE
                showResult(RESPONSE_STATUS_BAD_CONNECTION)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showResult(responseStatus: Int) {
        when (responseStatus) {
            RESPONSE_STATUS_BAD_CONNECTION -> {
                binding.connectionErrorGroup.visibility = View.VISIBLE
                binding.badSearchResultText.text =
                    applicationContext.resources.getText(R.string.connection_error)
                binding.historySearchContainer.visibility = View.GONE
                binding.badSearchResultImage.setImageResource(R.drawable.bad_connection_image)
                setDataChanged()
            }

            RESPONSE_STATUS_NOTHING_FOUND -> {
                binding.badSearchResultGroup.visibility = View.VISIBLE
                binding.badSearchResultText.text =
                    applicationContext.resources.getText(R.string.nothing_found)
                binding.historySearchContainer.visibility = View.GONE
                binding.badSearchResultImage.setImageResource(R.drawable.nothing_found_image)
                setDataChanged()
            }

            RESPONSE_STATUS_SUCCESS -> {
                binding.connectionErrorGroup.visibility = View.GONE
            }
        }
    }

    private fun searchDebounce() {
        mainHandler.removeCallbacks(searchRunnable)
        mainHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainHandler.postDelayed({isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setDataChanged() {
        trackList.clear()
        trackListAdapter.notifyDataSetChanged()
    }
}