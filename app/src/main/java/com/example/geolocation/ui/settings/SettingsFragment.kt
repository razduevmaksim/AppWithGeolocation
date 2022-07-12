package com.example.geolocation.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.geolocation.APP_PREFERENCES
import com.example.geolocation.APP_PREFERENCES_METRES
import com.example.geolocation.APP_PREFERENCES_MINUTES
import com.example.geolocation.MainActivity
import com.example.geolocation.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var preferences: SharedPreferences

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = this.requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        binding.sampleRateSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.sampleRateSeekBarValue.text = progress.toString()
                val editor = preferences.edit()
                editor.putLong(APP_PREFERENCES_MINUTES, progress.toLong())
                editor.apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        binding.accuracySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.accuracySeekBarValue.text = progress.toString()
                val editor = preferences.edit()
                editor.putFloat(APP_PREFERENCES_METRES, progress.toFloat())
                editor.apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}