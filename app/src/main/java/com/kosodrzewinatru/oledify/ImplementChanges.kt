package com.kosodrzewinatru.oledify

import android.content.Context
import android.preference.PreferenceManager
import androidx.appcompat.widget.AppCompatSeekBar
import com.kosodrzewinatru.oledify.activities.SettingsActivity

class ImplementChanges(context: Context, green: AppCompatSeekBar, blue: AppCompatSeekBar) {
    init {
        seekbarsStatus(context, green, blue)
    }

    private fun seekbarsStatus(context: Context, green: AppCompatSeekBar, blue: AppCompatSeekBar) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.RGB_SLIDERS_SWITCH, false)) {
            green.isEnabled = true
            blue.isEnabled = true
        } else {
            green.isEnabled = false
            blue.isEnabled = false
        }
    }
}