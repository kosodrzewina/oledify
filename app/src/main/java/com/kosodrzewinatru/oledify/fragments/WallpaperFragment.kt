package com.kosodrzewinatru.oledify.fragments

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.kosodrzewinatru.oledify.R
import kotlinx.android.synthetic.main.fragment_wallpaper.*

class WallpaperFragment(private val bitmap: Bitmap) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallpaper, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        wallpaper_home_button.setOnClickListener {
            WallpaperManager.getInstance(context)
                .setBitmap(bitmap, null, false, WallpaperManager.FLAG_SYSTEM)
            Toast.makeText(context, R.string.wallpaper_set, Toast.LENGTH_SHORT).show()
        }

        wallpaper_lock_button.setOnClickListener {
            WallpaperManager.getInstance(context)
                .setBitmap(bitmap, null, false, WallpaperManager.FLAG_LOCK)
            Toast.makeText(context, R.string.wallpaper_set, Toast.LENGTH_SHORT).show()
        }
    }
}