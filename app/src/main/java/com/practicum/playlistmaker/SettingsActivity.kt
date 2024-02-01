package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val message = getString(R.string.sampleMessageForShare)
        val url = getString(R.string.privacyUrl)
        val email = getString(R.string.sampleEmail)
        val subject = getString(R.string.sampleSubject)
        val body = getString(R.string.sampleBodyMessage)
        val switcher = findViewById<SwitchCompat>(R.id.darkThemeSwitcherCompat)
        val sharedPreferences =
            getSharedPreferences(SharedPreferencesData.sharedPreferencesThemeFile, MODE_PRIVATE)

        switcher.isChecked = sharedPreferences.getBoolean(SharedPreferencesData.darkThemeKey, false)

        switcher.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
        }

        val backButtonListener =
            findViewById<ImageButton>(R.id.back_button_settingsActivity).setOnClickListener {
                val backIntent = Intent(this, MainActivity::class.java)
                backIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                backIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(backIntent)
            }

        val shareButtonListener = findViewById<ImageView>(R.id.shareImageView).setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.setType("text/plain")
            val intentChooser = Intent.createChooser(shareIntent, " ")
            startActivity(intentChooser)
        }

        val supportButtonListener =
            findViewById<ImageView>(R.id.supportImageView).setOnClickListener {
                val supportIntent = Intent(Intent.ACTION_SENDTO)
                supportIntent.setData(Uri.parse("mailto:$email?subject=$subject&body=$body"))
                startActivity(supportIntent)
            }

        val privacyButtonListener = findViewById<ImageView>(R.id.privacyImageView).setOnClickListener {
            val privacyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(privacyIntent)
        }
    }
}