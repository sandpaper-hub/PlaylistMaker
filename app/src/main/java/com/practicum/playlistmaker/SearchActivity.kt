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
import androidx.core.view.isVisible
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
            showResult(ResponseStatus.SUCCESS)
            setDataChanged()
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
                badSearchResultImage.isVisible = true
                badSearchResultTextView.isVisible = true
                refreshSearchButton.isVisible = true
                badSearchResultTextView.text =
                    applicationContext.resources.getText(R.string.connection_error)
                badSearchResultImage.setImageResource(R.drawable.bad_connection_image)
                setDataChanged()
            }

            ResponseStatus.NOTHING_FOUND -> {
                badSearchResultImage.isVisible = true
                badSearchResultTextView.isVisible = true
                refreshSearchButton.isVisible = false
                badSearchResultTextView.text =
                    applicationContext.resources.getText(R.string.nothing_found)
                badSearchResultImage.setImageResource(R.drawable.nothing_found_image)
                setDataChanged()
            }

            ResponseStatus.SUCCESS -> {
                badSearchResultImage.isVisible = false
                badSearchResultTextView.isVisible = false
                refreshSearchButton.isVisible = false
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setDataChanged() {
        trackList.clear()
        trackListAdapter.notifyDataSetChanged()
    }
}