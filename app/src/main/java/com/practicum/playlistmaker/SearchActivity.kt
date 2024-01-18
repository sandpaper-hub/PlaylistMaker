package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val INSTANCE_STATE_KEY = "SAVED_RESULT"
    }

    var savedText = ""
    private var restoredText = ""

    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesSearchService = retrofit.create(ITunesSearchApi::class.java)

    private val trackList: ArrayList<Track> = ArrayList()

    private lateinit var badSearchResultImage: ImageView
    private lateinit var badSearchResultTextView: TextView
    private lateinit var refreshSearchButton: Button
    lateinit var searchEditText: EditText
    lateinit var trackListAdapter: TrackListAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<ImageButton>(R.id.back_button_searchActivity)
        searchEditText = findViewById(R.id.search_editText)
        val clearSearchButton = findViewById<ImageButton>(R.id.clear_search_ediText)
        val trackListRecyclerView = findViewById<RecyclerView>(R.id.trackListRecyclerView)

        trackListAdapter = TrackListAdapter(trackList)
        badSearchResultImage = findViewById(R.id.badSearchResultImage)
        badSearchResultTextView = findViewById(R.id.badSearchResultText)
        refreshSearchButton = findViewById(R.id.refresh_search_button)

        badSearchResultImage.visibility = GONE
        badSearchResultTextView.visibility = GONE
        refreshSearchButton.visibility = GONE

        trackListRecyclerView.adapter = trackListAdapter

        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            backIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            backIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(backIntent)
        }

        searchEditText.isSaveEnabled = false
        searchEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                clearSearchButton.visibility = GONE
            } else {
                clearSearchButton.visibility = VISIBLE
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO  not yet implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //TODO not yet implemented
            }

            override fun afterTextChanged(s: Editable?) {
                savedText = searchEditText.text.toString()
            }

        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doRequest()
            }
            false
        }

        clearSearchButton.visibility = GONE
        clearSearchButton.setOnClickListener {
            searchEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
            showBadResult("", 0)
            trackList.clear()
            trackListAdapter.notifyDataSetChanged()
        }

        refreshSearchButton.setOnClickListener {
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
        iTunesSearchService.search(searchEditText.text.toString()).enqueue(object :
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
                        showBadResult(
                            applicationContext.resources.getString(R.string.nothing_found),
                            R.drawable.nothing_found_image
                        )
                    } else {
                        showBadResult("", 0)
                    }
                } else {
                    showBadResult(
                        applicationContext.resources.getString(R.string.connection_error),
                        R.drawable.bad_connection_image
                    )
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showBadResult(
                    applicationContext.resources.getString(R.string.connection_error),
                    R.drawable.bad_connection_image
                )
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showBadResult(message: String, image: Int) {
        if (message.isNotEmpty()) {
            badSearchResultImage.visibility = VISIBLE
            badSearchResultTextView.visibility = VISIBLE
            badSearchResultTextView.text = message
            badSearchResultImage.setImageResource(image)
            if (message == applicationContext.resources.getString(R.string.connection_error)) {
                refreshSearchButton.visibility = VISIBLE
            } else {
                refreshSearchButton.visibility = GONE
            }
            trackList.clear()
            trackListAdapter.notifyDataSetChanged()
        } else {
            badSearchResultImage.visibility = GONE
            badSearchResultTextView.visibility = GONE
            refreshSearchButton.visibility = GONE
        }
    }
}