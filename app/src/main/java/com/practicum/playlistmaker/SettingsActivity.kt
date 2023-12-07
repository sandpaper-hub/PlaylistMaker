package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val message = getString(R.string.sampleMessageForShare)
        val url = getString(R.string.privacyUrl)
        val email = getString(R.string.sampleEmail)
        val subject = getString(R.string.sampleSubject)
        val body = getString(R.string.sampleBodyMessage)

        val backButtonListener =
            findViewById<ImageButton>(R.id.back_button_settingsActivity).setOnClickListener {
                val backIntent = Intent(this, MainActivity::class.java)
                backIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                backIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(backIntent)
            }

        val shareTextViewListener = findViewById<TextView>(R.id.shareTextView).setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.setType("text/plain")
            val intentChooser = Intent.createChooser(shareIntent, " ")
            startActivity(intentChooser)
        }

        val supportTextViewListener =
            findViewById<TextView>(R.id.supportTextView).setOnClickListener {
                val supportIntent = Intent(Intent.ACTION_SENDTO)
                supportIntent.setData(Uri.parse("mailto:$email?subject=$subject&body=$body"))
                startActivity(supportIntent)
            }

        val privacyTextViewListener = findViewById<TextView>(R.id.privacyTextView).setOnClickListener {
            val privacyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(privacyIntent)
        }
    }
}