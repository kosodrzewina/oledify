package com.kosodrzewinatru.oledify

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        saveButton.isEnabled = false

        // set imageView src via URI from MainActivity
        val selectedFileEdit = Uri.parse(intent.getStringExtra("selectedFileEdit"))

        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedFileEdit)
        val thumbnail = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)

        imageEditView.setImageBitmap(thumbnail)

        processButton.setOnClickListener {
            Processing().execute(thumbnail)
        }

        saveButton.setOnClickListener {
            saveBitmap(bitmap)
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

    private fun saveBitmap(bitmap: Bitmap) {

        val rootPath = Environment.getExternalStorageDirectory().toString()

        // create new file
        var file = File(rootPath + "/oledify_images")
        file.createNewFile()

        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
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

