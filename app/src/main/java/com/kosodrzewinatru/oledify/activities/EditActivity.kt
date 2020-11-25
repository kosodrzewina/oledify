package com.kosodrzewinatru.oledify.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.kosodrzewinatru.oledify.Edit
import com.kosodrzewinatru.oledify.ImplementStates
import com.kosodrzewinatru.oledify.R
import com.kosodrzewinatru.oledify.fragments.AboutFragment
import com.kosodrzewinatru.oledify.fragments.GalleryFragment
import com.kosodrzewinatru.oledify.fragments.LoadingFragment
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs
import kotlin.random.Random

/**
 * A class for the EditActivity.
 */
class EditActivity : AppCompatActivity() {
    // fragments
    private val galleryFragment = GalleryFragment()
    private val aboutFragment = AboutFragment()
    private val loadingFragment = LoadingFragment()

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

        // sets seekbars color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            intensity_seek_bar_green.progressDrawable.colorFilter = BlendModeColorFilter(
                this.resources.getColor(
                    R.color.color_green_light,
                    null
                ), BlendMode.SRC_ATOP
            )
            intensity_seek_bar_blue.progressDrawable.colorFilter = BlendModeColorFilter(
                this.resources.getColor(
                    R.color.color_blue_light,
                    null
                ), BlendMode.SRC_ATOP
            )
        } else {
            intensity_seek_bar_green.progressDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.color_green_light),
                PorterDuff.Mode.SRC_ATOP
            )
            intensity_seek_bar_blue.progressDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.color_blue_light),
                PorterDuff.Mode.SRC_ATOP
            )
        }

        // sets imageView src via URI from MainActivity
        val selectedFileEdit = Uri.parse(intent.getStringExtra("selectedFileEdit"))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, selectedFileEdit ?: return)
            bitmap = ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.ARGB_8888, true)
        } else {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedFileEdit)
        }

        thumbnail = Bitmap.createScaledBitmap(
            bitmap,
            bitmap.width / 2,
            bitmap.height / 2,
            true
        )

        save_button.setOnClickListener {
            loadingFragment.show(supportFragmentManager, "LOADING_START")

            CoroutineScope(Dispatchers.Default).launch {
                val processedBitmap =
                    when (sharedPrefs.getBoolean(SettingsActivity.RGB_SLIDERS_SWITCH, false)) {
                        true -> Edit().makeBlackToneCurve(
                            bitmap,
                            blackness_or_red_value.text.toString().toFloat(),
                            green_value.text.toString().toFloat(),
                            blue_value.text.toString().toFloat()
                        )
                        false -> Edit().makeBlackToneCurve(
                            bitmap, blackness_or_red_value
                                .text
                                .toString()
                                .toFloat()
                        )
                    }

                save(processedBitmap)
                loadingFragment.dismiss()
            }
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
        intensity_seek_bar_green.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
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
        intensity_seek_bar_blue.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
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
            this, blackness_or_red_value, intensity_seek_bar_maybe_red,
            intensity_seek_bar_green, intensity_seek_bar_blue,
            image_edit_view, bitmap, thumbnail
        )

        currentBitmap = when ((image_edit_view.drawable as BitmapDrawable).bitmap == bitmap) {
            true -> bitmap
            false -> thumbnail
        }

        // back-end for stuff in the bottom navigation view
        bottom_nav_edit.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.switch_editing -> {
                    if (galleryFragment.isVisible) {
                        supportFragmentManager.beginTransaction().remove(galleryFragment).commit()
                    }
                }

                R.id.switch_gallery -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_edit, galleryFragment).commit()
                }
            }

            return@setOnNavigationItemSelectedListener true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings_item -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)

                true
            }

            R.id.about_item -> {
                aboutFragment.show(supportFragmentManager, "ABOUT")

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
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
            this, blackness_or_red_value, intensity_seek_bar_maybe_red,
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

    override fun onBackPressed() {
        if (galleryFragment.isVisible) {
            supportFragmentManager.beginTransaction().remove(galleryFragment).commit()
        } else {
            super.onBackPressed()
        }
    }

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

            MediaScannerConnection.scanFile(
                this,
                arrayOf(file.toString()),
                arrayOf(file.name),
                null
            )

            Snackbar.make(
                snackbar_container,
                getString(R.string.saved),
                Snackbar.LENGTH_SHORT
            ).setTextColor(Color.WHITE).show()
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
                snackbar_container,
                getString(R.string.not_saved),
                Snackbar.LENGTH_SHORT
            ).setTextColor(Color.WHITE).show()
        }
    }
}
