package com.kosodrzewinatru.oledify.activities

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.kosodrzewinatru.oledify.ImplementStates
import com.kosodrzewinatru.oledify.R
import com.kosodrzewinatru.oledify.fragments.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    companion object {
        const val TONE_CURVE = "tone_curve"
        const val REAL_TIME_PROCESSING_SWITCH = "real_time_switch"
        const val RGB_SLIDERS_SWITCH = "rgb_sliders_switch"
        const val HIGH_RESOLUTION_SWITCH = "high_res_switch"
        const val THEME = "theme_drop_down"
        const val COLUMN_COUNT = "column_count_drop_down"
        private lateinit var preferences: SharedPreferences
    }

    private var onSharedPreferenceChangeListener =
        OnSharedPreferenceChangeListener { _, key ->
            if (key == THEME) {
                ImplementStates().themeState(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_SettingsScreen)

        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()

        preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    override fun onResume() {
        super.onResume()
        preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    override fun onPause() {
        super.onPause()
        preferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }
}