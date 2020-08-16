package com.kosodrzewinatru.oledify.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*
import android.widget.SeekBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kosodrzewinatru.oledify.Edit
import com.kosodrzewinatru.oledify.ImplementStates
import com.kosodrzewinatru.oledify.R
import com.kosodrzewinatru.oledify.fragments.GalleryFragment
import com.kosodrzewinatru.oledify.fragments.LanguagesFragment
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs
import kotlin.random.Random

/**
 * A class for the EditActivity.
 */
class EditActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView

    // fragments
    private val fragmentManager = supportFragmentManager
    private val languagesFragment = LanguagesFragment()
    private val galleryFragment = GalleryFragment()

    private lateinit var bitmap: Bitmap
    private lateinit var thumbnail: Bitmap

    /**
     * A variable keeping the current bitmap placed in the photoView.
     */
    lateinit var currentBitmap: Bitmap

    /**
     * An override function creating all of the most important things for the activity.
     *
     * @param savedInstanceState contains recent data in case of re-initializing the activity after
     * being shut down
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        // drawer itself
        bottomNav = findViewById(R.id.bottom_nav_edit)

        toolbar_edit.title = getString(R.string.app_name)

        // seekbars disabled by default
        intensity_seek_bar_green.isEnabled = false
        intensity_seek_bar_blue.isEnabled = false

        save_button.isEnabled = false

        // default value of intensity
        intensity_seek_bar_maybe_red.progress = 0
        blackness_or_red_value.text = intensity_seek_bar_maybe_red.progress.toString()

        intensity_seek_bar_green.progress = 0
        green_value.text = intensity_seek_bar_green.progress.toString()

        intensity_seek_bar_blue.progress = 0
        blue_value.text = intensity_seek_bar_blue.progress.toString()

        // set imageView src via URI from MainActivity
        val selectedFileEdit = Uri.parse(intent.getStringExtra("selectedFileEdit"))

        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedFileEdit)
        thumbnail = Bitmap.createScaledBitmap(
            bitmap,
            bitmap.width / 2,
            bitmap.height / 2,
            true
        )

        save_button.setOnClickListener {
            val drawable = image_edit_view.drawable

            save((drawable as BitmapDrawable).bitmap)
        }

        // seekbar for general or red intensity
        intensity_seek_bar_maybe_red.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    save_button.isEnabled = false
                }

                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (sharedPrefs.getBoolean(
                            SettingsActivity.REAL_TIME_PROCESSING_SWITCH,
                            true
                        )
                    ) {
                        blackness_or_red_value.text = p1.toString()
                        Processing().execute(currentBitmap)
                    } else {
                        blackness_or_red_value.text = p1.toString()
                    }

                    if (!sharedPrefs.getBoolean(SettingsActivity.RGB_SLIDERS_SWITCH, false)) {
                        intensity_seek_bar_green.progress = intensity_seek_bar_maybe_red.progress
                        intensity_seek_bar_blue.progress = intensity_seek_bar_maybe_red.progress
                    }
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    if (sharedPrefs.getBoolean(
                            SettingsActivity.REAL_TIME_PROCESSING_SWITCH,
                            true
                        )
                    ) {
                        save_button.isEnabled = true
                    } else {
                        save_button.isEnabled = true
                        Processing().execute(currentBitmap)
                    }
                }
            })

        // seekbar for green intensity
        intensity_seek_bar_green.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {
                save_button.isEnabled = false
            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (sharedPrefs.getBoolean(SettingsActivity.REAL_TIME_PROCESSING_SWITCH, true)) {
                    green_value.text = p1.toString()
                    Processing().execute(currentBitmap)
                } else {
                    green_value.text = p1.toString()
                }
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (sharedPrefs.getBoolean(SettingsActivity.REAL_TIME_PROCESSING_SWITCH, true)) {
                    save_button.isEnabled = true
                } else {
                    save_button.isEnabled = true
                    Processing().execute(currentBitmap)
                }
            }
        })

        // seekbar for blue intensity
        intensity_seek_bar_blue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {
                save_button.isEnabled = false
            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (sharedPrefs.getBoolean(SettingsActivity.REAL_TIME_PROCESSING_SWITCH, true)) {
                    blue_value.text = p1.toString()
                    Processing().execute(currentBitmap)
                } else {
                    blue_value.text = p1.toString()
                }
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (sharedPrefs.getBoolean(SettingsActivity.REAL_TIME_PROCESSING_SWITCH, true)) {
                    save_button.isEnabled = true
                } else {
                    save_button.isEnabled = true
                    Processing().execute(currentBitmap)
                }
            }
        })

        ImplementStates(
            this,
            intensity_seek_bar_green, intensity_seek_bar_blue,
            image_edit_view, bitmap, thumbnail
        )

        currentBitmap = when ((image_edit_view.drawable as BitmapDrawable).bitmap == bitmap) {
            true -> bitmap
            false -> thumbnail
        }

        ImplementStates().languageState(this)
    }

    /**
     * An override function called when the app has to be resumed. E.g. if user go back to the
     * activity from the SettingsActivity, this function retrieves states of the sliders and
     * processes the image once again.
     *
     * It eliminates the issue when the image was processed in the single slider mode and after
     * switching to the rgb the image is in its previous state.
     */
    override fun onResume() {
        super.onResume()

        ImplementStates(
            this,
            intensity_seek_bar_green, intensity_seek_bar_blue,
            image_edit_view, bitmap, thumbnail
        )

        currentBitmap = when ((image_edit_view.drawable as BitmapDrawable).bitmap == bitmap) {
            true -> bitmap
            false -> thumbnail
        }

        Processing().execute(currentBitmap)

        val sharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(applicationContext)
        if (!sharedPrefs.getBoolean(SettingsActivity.RGB_SLIDERS_SWITCH, false)) {
            intensity_seek_bar_green.progress = intensity_seek_bar_maybe_red.progress
            intensity_seek_bar_blue.progress = intensity_seek_bar_maybe_red.progress
        }
    }

    /**
     * An override function for selecting items from the drawer.
     *
     * @param p0 selected item
     * @return true if the chosen item should be displayed as a selected item or false if not
     */
//    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
//        when (p0.itemId) {
//            R.id.switchEditing -> {
//                if (galleryFragment.isVisible)
//                    supportFragmentManager.beginTransaction().remove(galleryFragment).commit()
//
//                drawerEdit.closeDrawer(GravityCompat.START)
//            }
//
//            R.id.language -> languagesFragment.show(fragmentManager, "LIST")
//
//            R.id.switchGallery -> {
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragmentContainer, galleryFragment).commit()
//                drawerEdit.closeDrawer(GravityCompat.START)
//            }
//
//            R.id.processingSettings -> {
//                val intent = Intent(this, SettingsActivity::class.java)
//                startActivity(intent)
//                drawerEdit.closeDrawer(GravityCompat.START)
//            }
//        }
//
//        return true
//    }

    /**
     * An override function called when back button is pressed. If so, the drawer is being closed
     * if it's open.
     */
//    override fun onBackPressed() {
//        when {
//            drawerEdit.isDrawerOpen(GravityCompat.START) -> drawerEdit.closeDrawer(GravityCompat.START)
//            galleryFragment.isVisible -> {
//                supportFragmentManager.beginTransaction().remove(galleryFragment).commit()
//                navViewEdit.menu.getItem(0).isChecked = true
//            }
//            else -> super.onBackPressed()
//        }
//    }

    // asynchronous class for heavy processing tasks
    internal inner class Processing : AsyncTask<Bitmap, Void, Bitmap>() {
        private val sharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(applicationContext)!!

        override fun doInBackground(vararg params: Bitmap?): Bitmap? {
            return when (sharedPrefs.getBoolean(SettingsActivity.RGB_SLIDERS_SWITCH, false)) {
                true -> Edit().makeBlackToneCurve(
                    params[0] ?: return null,
                    blackness_or_red_value.text.toString().toFloat(),
                    green_value.text.toString().toFloat(),
                    blue_value.text.toString().toFloat()
                )
                false -> Edit().makeBlackToneCurve(
                    params[0] ?: return null, blackness_or_red_value
                        .text
                        .toString()
                        .toFloat()
                )
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            image_edit_view.setImageBitmap(result)
            save_button.isEnabled = true
        }
    }

    private fun save(bitmap: Bitmap) {
        if (ActivityCompat.checkSelfPermission(
                this@EditActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val filePath = Environment
                .getExternalStorageDirectory()
                .absolutePath + "/Pictures/Oledify"
            val directory = File(filePath)

            if (!directory.exists()) {
                directory.mkdir()
            }

            var id = (abs(Random.nextDouble()) * 10000).toInt()
            var file = File(directory, "oledify_$id.png")
            var idExists = true

            while (idExists) {
                idExists = false

                for (currentFile in directory.listFiles()) {
                    if (currentFile == file) {
                        idExists = true
                        break
                    }
                }

                id = (abs(Random.nextDouble()) * 10000).toInt()
                file = File(directory, "oledify_$id.png")
            }

            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)

            fileOutputStream.flush()
            fileOutputStream.close()

            val intentMediaScannerConnection = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)

            intentMediaScannerConnection.data = Uri.fromFile(file)
            sendBroadcast(intentMediaScannerConnection)

            Snackbar.make(bottomNav, "Image saved successfully!", Snackbar.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this@EditActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        }
    }

    /**
     * An override function to inform user if the permission for writing in storage was granted.
     *
     * @param requestCode code passed from requestPermissions
     * @param permissions array of requested permissions
     * @param grantResults array with information if permissions were granted
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (
            !(requestCode == 100 && Environment.getExternalStorageState() ==
                    Environment.MEDIA_MOUNTED)
        ) {
            Snackbar.make(
                bottomNav,
                getString(R.string.not_saved),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}
