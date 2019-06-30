package com.kosodrzewinatru.oledify

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.jar.Manifest

class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        saveButton.isEnabled = false

        // default value of intensity
        intensitySeekBar.progress = 18
        blacknessValue.text = intensitySeekBar.progress.toString()

        // set imageView src via URI from MainActivity
        val selectedFileEdit = Uri.parse(intent.getStringExtra("selectedFileEdit"))

        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedFileEdit)
        val thumbnail = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)

        imageEditView.setImageBitmap(thumbnail)

        processButton.setOnClickListener {
            Processing().execute(thumbnail)
        }

        saveButton.setOnClickListener {
            saveToStorage()
        }

        intensitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                processButton.isEnabled = false
                saveButton.isEnabled = false
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                blacknessValue.text = progress.toString()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                processButton.isEnabled = true
                saveButton.isEnabled = true
            }
        })
    }

    private fun saveToStorage() {

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("check_permission", "GRANTED")
        } else {
            Log.d("check_permission", "DENIED")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 100 && Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val storageDirectory = Environment.getExternalStorageDirectory().toString()
            val file = File(storageDirectory, "test.jpg")
            val stream: OutputStream = FileOutputStream(file)

            // get the original bitmap once again
            val selectedFileEdit = Uri.parse(intent.getStringExtra("selectedFileEdit"))
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedFileEdit)

            bitmap = Processing().Editing().makeBlack(bitmap, blacknessValue.text.toString().toFloat())

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            stream.flush()
            stream.close()

            Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Something went wrong. Image cannot be saved.", Toast.LENGTH_SHORT).show()
        }
    }

    // asynchronous class for heavy processing tasks
    internal inner class Processing : AsyncTask<Bitmap, Void, Bitmap>() {
        override fun onPreExecute() {
            super.onPreExecute()

            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Bitmap?): Bitmap? {
            return Editing().makeBlack(params[0]!!, (765 * (blacknessValue.text.toString().toFloat() / 100)))
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)

            imageEditView.setImageBitmap(result)

            progressBar.visibility = View.INVISIBLE
            saveButton.isEnabled = true
            Toast.makeText(this@EditActivity, "Finished!", Toast.LENGTH_SHORT).show()
        }

        // inner class with all functions related to editing
        internal inner class Editing {

            // main function responsible for processing bitmap
            fun makeBlack(bitmap: Bitmap, intensity: Float): Bitmap {

                // create a mutable copy of the bitmap
                val processed = bitmap.copy(Bitmap.Config.ARGB_8888, true)

                // check every single pixel
                for (y in 0 until bitmap.height) {
                    for (x in 0 until bitmap.width) {
                        val currentPixel = bitmap.getPixel(x, y)

                        val red = Color.red(currentPixel)
                        val green = Color.green(currentPixel)
                        val blue = Color.blue(currentPixel)

                        if (red + green + blue <= intensity) {
                            processed.setPixel(x, y, Color.rgb(0, 0, 0))
                        }
                    }
                }

                return processed
            }
        }
    }
}
