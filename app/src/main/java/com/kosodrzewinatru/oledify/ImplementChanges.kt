package com.kosodrzewinatru.oledify

import android.content.Context
import android.graphics.Bitmap
import android.preference.PreferenceManager
import androidx.appcompat.widget.AppCompatSeekBar
import com.kosodrzewinatru.oledify.activities.SettingsActivity
import com.github.chrisbanes.photoview.PhotoView

class ImplementChanges(context: Context,
                       green: AppCompatSeekBar, blue: AppCompatSeekBar,
                       photoView: PhotoView, bitmap: Bitmap, thumbnail: Bitmap) {
    init {
        seekbarsState(context, green, blue)
        resState(context, photoView, bitmap, thumbnail)
    }

    private fun seekbarsState(context: Context, green: AppCompatSeekBar, blue: AppCompatSeekBar) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.RGB_SLIDERS_SWITCH, false)) {
            green.isEnabled = true
            blue.isEnabled = true
        } else {
            green.isEnabled = false
            blue.isEnabled = false
        }
    }

    private fun resState(context: Context, photoView: PhotoView, bitmap: Bitmap, thumbnail: Bitmap) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.HIGH_RESOLUTION_SWITCH, false)) {
            photoView.setImageBitmap(bitmap)
        } else {
            photoView.setImageBitmap(thumbnail)
        }
    }
}