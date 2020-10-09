package com.kosodrzewinatru.oledify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.github.chrisbanes.photoview.PhotoView
import com.kosodrzewinatru.oledify.activities.SettingsActivity
import java.util.*


class ImplementStates {
    constructor()

    constructor(
        context: Context,
        redIndicator: AppCompatTextView,
        red: AppCompatSeekBar,
        green: AppCompatSeekBar,
        blue: AppCompatSeekBar,
        photoView: PhotoView,
        bitmap: Bitmap,
        thumbnail: Bitmap
    ) {
        seekbarsState(context, redIndicator, red, green, blue)
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

    fun languageState(activity: Activity) {
        val currentLocale = activity.resources.getString(R.string.language)
        val targetLocale = PreferenceManager.getDefaultSharedPreferences(activity)
            .getString(SettingsActivity.LOCALE, "en")

        if (currentLocale != targetLocale) {
            val config = activity.resources.configuration

            val targetLocaleList = targetLocale?.split('_')

            if ((targetLocaleList ?: return).size > 1) {
                Locale.setDefault(Locale(targetLocaleList[0], targetLocaleList[1]))
                config.setLocale(Locale(targetLocaleList[0], targetLocaleList[1]))
            } else {
                Locale.setDefault(Locale(targetLocaleList[0]))
                config.setLocale(Locale(targetLocaleList[0]))
            }

            activity.resources.updateConfiguration(config, activity.resources.displayMetrics)

            activity.finish()
            activity.startActivity(activity.intent)
        }
    }

    private fun seekbarsState(
        context: Context,
        redIndicator: AppCompatTextView,
        red: AppCompatSeekBar,
        green: AppCompatSeekBar,
        blue: AppCompatSeekBar
    ) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                SettingsActivity.RGB_SLIDERS_SWITCH,
                false
            )
        ) {
            redIndicator.setTextColor(context.resources.getColor(R.color.color_red_light, null))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                red.progressDrawable.colorFilter = BlendModeColorFilter(
                    context.resources.getColor(
                        R.color.color_red_light,
                        null
                    ), BlendMode.SRC_ATOP
                )
                red.thumb.colorFilter = BlendModeColorFilter(
                    context.resources.getColor(
                        R.color.color_red_dark,
                        null
                    ), BlendMode.SRC_ATOP
                )
            } else {
                red.progressDrawable.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_red_light),
                    PorterDuff.Mode.SRC_ATOP
                )
                red.thumb.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_red_dark),
                    PorterDuff.Mode.SRC_ATOP
                )
            }

            green.isEnabled = true
            blue.isEnabled = true
        } else {
            redIndicator.setTextColor(context.resources.getColor(R.color.color_accent, null))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                red.progressDrawable.colorFilter = BlendModeColorFilter(
                    context.resources.getColor(
                        R.color.color_accent,
                        null
                    ), BlendMode.SRC_ATOP
                )
                red.thumb.colorFilter = BlendModeColorFilter(
                    context.resources.getColor(
                        R.color.color_accent,
                        null
                    ), BlendMode.SRC_ATOP
                )
            } else {
                red.progressDrawable.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_accent),
                    PorterDuff.Mode.SRC_ATOP
                )
                red.thumb.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_accent),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
            green.isEnabled = false
            blue.isEnabled = false
        }
    }

    private fun resState(
        context: Context,
        photoView: PhotoView,
        bitmap: Bitmap,
        thumbnail: Bitmap
    ) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                SettingsActivity.HIGH_RESOLUTION_SWITCH,
                false
            )
        ) {
            photoView.setImageBitmap(bitmap)
        } else {
            photoView.setImageBitmap(thumbnail)
        }
    }

    fun themeState(context: Context) {
        when (PreferenceManager.getDefaultSharedPreferences(context).getString(
            SettingsActivity.THEME,
            "theme_system"
        )) {
            "theme_light" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            "theme_dark" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}
