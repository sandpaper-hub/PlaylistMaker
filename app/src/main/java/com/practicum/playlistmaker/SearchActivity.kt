package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    var savedText = ""
    private var restoredText = ""
    private val trackList: ArrayList<Track> = TrackListMockObject.listOfTrack

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<ImageButton>(R.id.back_button_searchActivity)
        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            backIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            backIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(backIntent)
        }

        val searchEditText = findViewById<EditText>(R.id.search_editText)
        searchEditText.isSaveEnabled = false
        val clearSearchButton = findViewById<ImageButton>(R.id.clear_search_ediText)
        clearSearchButton.visibility = GONE

        clearSearchButton.setOnClickListener {
            searchEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        searchEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                clearSearchButton.visibility = GONE
            } else {
                clearSearchButton.visibility = VISIBLE
            }
        }

        val trackListRecyclerView = findViewById<RecyclerView>(R.id.trackListRecyclerView)
        val trackListAdapter = TrackListAdapter(trackList)
        trackListRecyclerView.adapter = trackListAdapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //not yet implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //not yet implemented
            }

            override fun afterTextChanged(s: Editable?) {
                savedText = searchEditText.text.toString()
            }

        })
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

    companion object {
        const val INSTANCE_STATE_KEY = "SAVED_RESULT"
    }
}