package com.practicum.playlistmaker.settings.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.presentation.model.SettingsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val viewModel by viewModel<SettingsViewModel>()
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner){
            render(it)
        }
        val message = getString(R.string.sampleMessageForShare)
        val url = getString(R.string.privacyUrl)
        val email = getString(R.string.sampleEmail)
        val subject = getString(R.string.sampleSubject)
        val body = getString(R.string.sampleBodyMessage)

        binding.darkThemeSwitcherCompat.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
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

    private fun render(state: SettingsState) {
        when (state) {
            is SettingsState.DarkTheme -> switchTheme(state.isChecked)
        }
    }

    private fun switchTheme(isChecked: Boolean) {
        binding.darkThemeSwitcherCompat.isChecked = isChecked
    }
}