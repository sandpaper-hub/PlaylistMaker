package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val message = getString(R.string.sampleMessageForShare)
        val url = getString(R.string.privacyUrl)
        val email = getString(R.string.sampleEmail)
        val subject = getString(R.string.sampleSubject)
        val body = getString(R.string.sampleBodyMessage)

        viewModel = ViewModelProvider(
            this, SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        binding.darkThemeSwitcherCompat.isChecked = viewModel.isChecked()

        binding.darkThemeSwitcherCompat.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
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