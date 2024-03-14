package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val message = getString(R.string.sampleMessageForShare)
        val url = getString(R.string.privacyUrl)
        val email = getString(R.string.sampleEmail)
        val subject = getString(R.string.sampleSubject)
        val body = getString(R.string.sampleBodyMessage)
        val sharedPreferences =
            getSharedPreferences(SharedPreferencesData.SHARED_PREFERENCES_THEME_KEY, MODE_PRIVATE)

        binding.darkThemeSwitcherCompat.isChecked = sharedPreferences.getBoolean(SharedPreferencesData.DARK_THEME_KEY, false)

        binding.darkThemeSwitcherCompat.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
        }

        binding.backButtonSettingsActivity.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

        binding.shareImageView.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.setType("text/plain")
            val intentChooser = Intent.createChooser(shareIntent, " ")
            startActivity(intentChooser)
        }

        binding.supportImageView.setOnClickListener {
                val supportIntent = Intent(Intent.ACTION_SENDTO)
                supportIntent.setData(Uri.parse("mailto:$email?subject=$subject&body=$body"))
                startActivity(supportIntent)
            }

        binding.privacyImageView.setOnClickListener {
            val privacyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(privacyIntent)
        }
    }
}