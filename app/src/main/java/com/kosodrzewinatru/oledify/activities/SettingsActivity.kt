package com.kosodrzewinatru.oledify.activities

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.kosodrzewinatru.oledify.fragments.SettingsFragment
import java.util.*

class SettingsActivity : AppCompatActivity() {
    companion object {
        val REAL_TIME_PROCESSING_SWITCH = "real_time_switch"
        val RGB_SLIDERS_SWITCH = "rgb_sliders_switch"
        val HIGH_RESOLUTION_SWITCH = "high_res_switch"
        val LOCALE = "locale"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
    }

    fun localeToSharedPreferences(context: Context, locale: Locale) {
        val editor = PreferenceManager
            .getDefaultSharedPreferences(context)
            .edit()

        editor.putString(LOCALE, locale.toString())
        editor.apply()
    }
}