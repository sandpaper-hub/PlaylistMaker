package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

    lateinit var trackListAdapter: TrackListAdapter
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences =
            getSharedPreferences(SharedPreferencesData.sharedPreferencesHistoryFile, MODE_PRIVATE)
        val historyPreferences = HistoryPreferences(sharedPreferences)

        val json = sharedPreferences.getString(SharedPreferencesData.newHistoryItemKey, null)
        historyArray = if (json == null) {
            ArrayList()
        } else {
            Transformer.createArrayListFromJson(json)
        }

        val historyAdapter =
            TrackListAdapter(historyArray, object : TrackListAdapter.OnTrackClickListener {
                override fun onItemClick(track: Track) {
                    val playerIntent = Intent(applicationContext, PlayerActivity::class.java)
                    playerIntent.putExtra(INTENT_EXTRA_KEY, track)
                    startActivity(playerIntent)
                }
            })

        binding.historyRecycler.adapter = historyAdapter

        sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == SharedPreferencesData.newHistoryItemKey) {
                val jsonArray =
                    sharedPreferences.getString(SharedPreferencesData.newHistoryItemKey, null)
                if (jsonArray != null) {
                    historyAdapter.trackList = Transformer.createArrayListFromJson(jsonArray)
                }
                historyAdapter.notifyDataSetChanged()
            }
        }

        binding.clearHistory.setOnClickListener {
            historyPreferences.clearData()
            binding.historySearchContainer.isVisible = false
        }

        trackListAdapter =
            TrackListAdapter(trackList, object : TrackListAdapter.OnTrackClickListener {
                override fun onItemClick(track: Track) {
                    historyPreferences.addTrack(historyArray, track)
                    val playerIntent = Intent(applicationContext, PlayerActivity::class.java)
                    playerIntent.putExtra(
                        INTENT_EXTRA_KEY,
                        track
                    )
                    startActivity(playerIntent)
                }
            })

        binding.trackListRecyclerView.adapter = trackListAdapter

        binding.backButtonSearchActivity.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.searchEditText.isSaveEnabled = false
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                binding.clearSearchEdiText.isVisible = false
                binding.trackListRecyclerView.isVisible = false
            } else {
                binding.clearSearchEdiText.isVisible = true
                binding.trackListRecyclerView.isVisible = true
            }
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            binding.historySearchContainer.isVisible =
                hasFocus && binding.searchEditText.text.isEmpty() && historyArray.isNotEmpty()

        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //not yet implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.historySearchContainer.isVisible =
                    (binding.searchEditText.hasFocus() && s?.isEmpty() == true) && historyAdapter.trackList.isNotEmpty() == true
                binding.connectionErrorGroup.isVisible = false
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
            showResult(ResponseStatus.SUCCESS)
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
        iTunesSearchService.search(binding.searchEditText.text.toString()).enqueue(object :
            Callback<TrackResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {
                if (response.code() == 200) {
                    trackList.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        trackList.addAll(response.body()?.results!!)
                        trackListAdapter.notifyDataSetChanged()
                    }
                    if (trackList.isEmpty()) {
                        showResult(ResponseStatus.NOTHING_FOUND)
                    } else {
                        showResult(ResponseStatus.SUCCESS)
                    }
                } else {
                    showResult(ResponseStatus.BAD_CONNECTION)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showResult(ResponseStatus.BAD_CONNECTION)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showResult(responseStatus: Enum<ResponseStatus>) {
        when (responseStatus) {
            ResponseStatus.BAD_CONNECTION -> {
                binding.connectionErrorGroup.isVisible = true
                binding.badSearchResultText.text =
                    applicationContext.resources.getText(R.string.connection_error)
                binding.historySearchContainer.isVisible = false
                binding.badSearchResultImage.setImageResource(R.drawable.bad_connection_image)
                setDataChanged()
            }

            ResponseStatus.NOTHING_FOUND -> {
                binding.badSearchResultGroup.isVisible = true
                binding.badSearchResultText.text =
                    applicationContext.resources.getText(R.string.nothing_found)
                binding.historySearchContainer.isVisible = false
                binding.badSearchResultImage.setImageResource(R.drawable.nothing_found_image)
                setDataChanged()
            }

            ResponseStatus.SUCCESS -> {
                binding.connectionErrorGroup.isVisible = false
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setDataChanged() {
        trackList.clear()
        trackListAdapter.notifyDataSetChanged()
    }
}