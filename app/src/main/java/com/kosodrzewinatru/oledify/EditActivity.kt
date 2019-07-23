package com.kosodrzewinatru.oledify

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import kotlinx.android.synthetic.main.activity_edit.*
import android.util.Log
import android.view.MenuItem
import android.widget.SeekBar
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class EditActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout

    private val fragmentManager = supportFragmentManager
    private val languagesFragment = LanguagesFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // drawer itself
        drawer = findViewById(R.id.drawerEdit)
        val navigationView = findViewById<NavigationView>(R.id.navViewEdit)
        navigationView.setNavigationItemSelectedListener(this)


        toolbarEdit.title = getString(R.string.app_name)

        // hamburger icon
        var toggle = ActionBarDrawerToggle(this, drawerEdit, toolbarEdit, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerEdit.addDrawerListener(toggle)
        toggle.syncState()

        saveButton.isEnabled = false

        // default value of intensity
        intensitySeekBar.progress = 0
        blacknessValue.text = intensitySeekBar.progress.toString()

        // set imageView src via URI from MainActivity
        val selectedFileEdit = Uri.parse(intent.getStringExtra("selectedFileEdit"))

        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedFileEdit)
        val thumbnail = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)

        imageEditView.setImageBitmap(thumbnail)

        saveButton.setOnClickListener {
            saveToStorage()
        }

        intensitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                saveButton.isEnabled = false
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val switchRealTime = findViewById<SwitchCompat>(R.id.realTime)

                if (switchRealTime.isChecked) {
                    blacknessValue.text = progress.toString()
                    Processing().execute(thumbnail)
                } else {
                    blacknessValue.text = progress.toString()
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val switchRealTime = findViewById<SwitchCompat>(R.id.realTime)

                if (switchRealTime.isChecked) {
                    saveButton.isEnabled = true
                } else {
                    saveButton.isEnabled = true
                    Processing().execute(thumbnail)
                }
            }
        })
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId) {
            R.id.language -> languagesFragment.show(fragmentManager, "LIST")
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

    private fun saveToStorage() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            if (requestCode == 100 && Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val storageDirectory = Environment.getExternalStorageDirectory().toString()
            Log.d("PATH", storageDirectory)

            val file = File(storageDirectory, "test.jpg")
            val stream: OutputStream = FileOutputStream(file)

            // get the original bitmap once again
            val selectedFileEdit = Uri.parse(intent.getStringExtra("selectedFileEdit"))
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedFileEdit)

            bitmap = Processing().Editing().makeBlack(bitmap, blacknessValue.text.toString().toFloat())

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            stream.flush()
            stream.close()

            Snackbar.make(drawerEdit, getString(R.string.saved), Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(drawerEdit, getString(R.string.not_saved), Snackbar.LENGTH_SHORT).show()
        }
    }

    // asynchronous class for heavy processing tasks
    internal inner class Processing : AsyncTask<Bitmap, Void, Bitmap>() {
        override fun doInBackground(vararg params: Bitmap?): Bitmap? {
            return Editing().makeBlack(params[0]!!, blacknessValue.text.toString().toFloat())
        }

        override fun onPostExecute(result: Bitmap?) {
            imageEditView.setImageBitmap(result)

            saveButton.isEnabled = true
        }

        // inner class with all functions related to editing
        internal inner class Editing {

            // main function responsible for processing bitmap
            fun makeBlack(bitmap: Bitmap, intensity: Float): Bitmap {
                val intensity = intensity * 765 / 100

                val pixels = IntArray(bitmap.height * bitmap.width)

                bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

                for (i in 0 until pixels.size) {
                    val red = Color.red(pixels[i])
                    val green = Color.green(pixels[i])
                    val blue = Color.blue(pixels[i])

                    if (red + green + blue <= intensity) {
                        pixels[i] = -16777214
                    }
                }

                val processed = bitmap.copy(Bitmap.Config.ARGB_8888, true)

                processed.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

                return processed
            }
        }
    }
}
