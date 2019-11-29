package com.kosodrzewinatru.oledify.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import kotlinx.android.synthetic.main.activity_edit.*
import android.util.Log
import android.view.MenuItem
import android.widget.SeekBar
import com.kosodrzewinatru.oledify.Editing
import com.kosodrzewinatru.oledify.R
import com.kosodrzewinatru.oledify.fragments.ComingSoonFragment
import com.kosodrzewinatru.oledify.fragments.LanguagesFragment
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs
import kotlin.random.Random

class EditActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout

    private val fragmentManager = supportFragmentManager
    private val languagesFragment = LanguagesFragment()
    private val comingSoonFragment = ComingSoonFragment()

    val SHARED_PREFS = "sharedPrefs"

    //shared prefs elements
    val IS_FIRST_LAUNCH = "isFirstLaunch"
    val IS_RGB = "isRgb"
    val IS_REAL_TIME_PROCESSING = "isRealTimeProcessing"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // drawer itself
        drawer = findViewById(R.id.drawerEdit)
        val navigationView = findViewById<NavigationView>(R.id.navViewEdit)
        navigationView.setNavigationItemSelectedListener(this)

        toolbarEdit.title = getString(R.string.app_name)

        // seekbars disabled by default
        intensitySeekBarGreen.isEnabled = false
        intensitySeekBarBlue.isEnabled = false

        // hamburger icon
        val toggle = ActionBarDrawerToggle(
            this,
            drawerEdit,
            toolbarEdit,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerEdit.addDrawerListener(toggle)
        toggle.syncState()

        saveButton.isEnabled = false

        // default value of intensity
        intensitySeekBarMaybeRed.progress = 0
        blacknessOrRedValue.text = intensitySeekBarMaybeRed.progress.toString()

        intensitySeekBarGreen.progress = 0
        greenValue.text = intensitySeekBarGreen.progress.toString()

        intensitySeekBarBlue.progress = 0
        blueValue.text = intensitySeekBarBlue.progress.toString()

        // set imageView src via URI from MainActivity
        val selectedFileEdit = Uri.parse(intent.getStringExtra("selectedFileEdit"))

        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedFileEdit)
        val thumbnail = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)

        imageEditView.setImageBitmap(thumbnail)

        saveButton.setOnClickListener {
            val drawable = imageEditView.drawable

            save((drawable as BitmapDrawable).bitmap)
        }

        // seekbar for general or red intensity
        intensitySeekBarMaybeRed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                saveButton.isEnabled = false
            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getBoolean(IS_REAL_TIME_PROCESSING, false)) {
                    blacknessOrRedValue.text = p1.toString()
                    Processing().execute(thumbnail)
                } else {
                    blacknessOrRedValue.text = p1.toString()
                }
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getBoolean(IS_REAL_TIME_PROCESSING, false)) {
                    saveButton.isEnabled = true
                } else {
                    saveButton.isEnabled = true
                    Processing().execute(thumbnail)
                }
            }
        })

        // seekbar for green intensity
        intensitySeekBarGreen.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(p0: SeekBar?) {
                saveButton.isEnabled = false
            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val switchRealTime = findViewById<SwitchCompat>(R.id.realTime)

                if (switchRealTime.isChecked) {
                    greenValue.text = p1.toString()
                    Processing().execute(thumbnail)
                } else {
                    greenValue.text = p1.toString()
                }
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                val switchRealTime = findViewById<SwitchCompat>(R.id.realTime)

                if (switchRealTime.isChecked) {
                    saveButton.isEnabled = true
                } else {
                    saveButton.isEnabled = true
                    Processing().execute(thumbnail)
                }
            }
        })

        // seekbar for blue intensity
        intensitySeekBarBlue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(p0: SeekBar?) {
                saveButton.isEnabled = false
            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val switchRealTime = findViewById<SwitchCompat>(R.id.realTime)

                if (switchRealTime.isChecked) {
                    blueValue.text = p1.toString()
                    Processing().execute(thumbnail)
                } else {
                    blueValue.text = p1.toString()
                }
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                val switchRealTime = findViewById<SwitchCompat>(R.id.realTime)

                if (switchRealTime.isChecked) {
                    saveButton.isEnabled = true
                } else {
                    saveButton.isEnabled = true
                    Processing().execute(thumbnail)
                }
            }
        })

        val rgbSwitch = navigationView.menu.findItem(R.id.rgbSliders)
        var isRGBChecked = false

        // listener for rgbSwitch
        rgbSwitch.actionView.setOnClickListener {
            Log.d("RGB_SWITCH_STATE", rgbSwitch.actionView.isPressed.toString())
            rgbSwitch.actionView.isPressed = false

            if (isRGBChecked) {
                intensitySeekBarGreen.isEnabled = false
                intensitySeekBarBlue.isEnabled = false
                isRGBChecked = false
            } else {
                intensitySeekBarGreen.isEnabled = true
                intensitySeekBarBlue.isEnabled = true
                isRGBChecked = true
            }
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId) {
            R.id.language -> languagesFragment.show(fragmentManager, "LIST")
            R.id.switchGallery -> comingSoonFragment.show(supportFragmentManager, "FEATURE")
        }

        return true
    }

    // if back button is pressed and the drawer is open, close the drawer
    override fun onBackPressed() {
        if (drawerEdit.isDrawerOpen(GravityCompat.START)) {
            drawerEdit.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    // asynchronous class for heavy processing tasks
    internal inner class Processing : AsyncTask<Bitmap, Void, Bitmap>() {
        override fun doInBackground(vararg params: Bitmap?): Bitmap? {
            return when (getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getBoolean(IS_RGB, false)) {
                true -> Editing().makeBlackRGB(
                    params[0]!!,
                    blacknessOrRedValue.text.toString().toFloat(),
                    greenValue.text.toString().toFloat(),
                    blueValue.text.toString().toFloat())
                false -> Editing().makeBlack(params[0]!!, blacknessOrRedValue.text.toString().toFloat())
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            imageEditView.setImageBitmap(result)

            saveButton.isEnabled = true
        }
    }

    private fun save(bitmap: Bitmap) {
        if (ActivityCompat.checkSelfPermission(
                this@EditActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/Oledify"
            val directory = File(filePath)

            if (!directory.exists()) {
                directory.mkdir()
            }

            val id = (abs(Random.nextDouble()) * 10000).toInt()

            val file = File(directory, "oledify_$id.png")
            val fileOutputStream = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)

            fileOutputStream.flush()
            fileOutputStream.close()

            Snackbar.make(drawerEdit, "Image saved successfully!", Snackbar.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this@EditActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 100 && Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val drawable = imageEditView.drawable
            val bitmap = (drawable as BitmapDrawable).bitmap

            save(bitmap)
        } else {
            Snackbar.make(drawerEdit, getString(R.string.not_saved), Snackbar.LENGTH_SHORT).show()
        }
    }
}
