package com.kosodrzewinatru.oledify.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.kosodrzewinatru.oledify.fragments.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    companion object {
        val REAL_TIME_PROCESSING_SWITCH = "real_time_switch"
        val RGB_SLIDERS_SWITCH = "rgb_sliders_switch"
        val HIGH_RESOLUTION_SWITCH = "high_res_switch"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        val realTime = sharedPrefs.getBoolean(SettingsActivity.REAL_TIME_PROCESSING_SWITCH, false)
        val rgbSliders = sharedPrefs.getBoolean(SettingsActivity.RGB_SLIDERS_SWITCH, false)
        val highRes = sharedPrefs.getBoolean(SettingsActivity.HIGH_RESOLUTION_SWITCH, false)
    }
}