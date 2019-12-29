package com.kosodrzewinatru.oledify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.preference.PreferenceManager
import androidx.appcompat.widget.AppCompatSeekBar
import com.kosodrzewinatru.oledify.activities.SettingsActivity
import com.github.chrisbanes.photoview.PhotoView
import java.util.*

class ImplementStates {
    constructor()

    constructor(context: Context, green: AppCompatSeekBar, blue: AppCompatSeekBar,
                photoView: PhotoView, bitmap: Bitmap, thumbnail: Bitmap) {
        seekbarsState(context, green, blue)
        resState(context, photoView, bitmap, thumbnail)
    }

    fun changeLanguage(activity: Activity, intent: Intent, resources: Resources, locale: Locale) {
        val config = resources.configuration

        Locale.setDefault(locale)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        activity.finish()
        activity.startActivity(intent)
    }

    // probably doesn't work... for now
    fun languageState(context: Context, activity: Activity) {
        val currentLocale = context.resources.getString(R.string.language)
        val targetLocale = PreferenceManager.getDefaultSharedPreferences(context).
            getString(SettingsActivity.LOCALE, "en")

        if (currentLocale != targetLocale) {
            val config = activity.resources.configuration

            Locale.setDefault(Locale.forLanguageTag(targetLocale))
            config.setLocale(Locale.forLanguageTag(targetLocale))
            activity.resources.updateConfiguration(config, activity.resources.displayMetrics)

            activity.finish()
            activity.startActivity(activity.intent)
        }
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
