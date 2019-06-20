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
            Saving().execute(bitmap)
        }
    }

    // async for saving bitmap to file
    internal inner class Saving : AsyncTask<Bitmap, Void, Void>() {
        override fun onPreExecute() {
            super.onPreExecute()

            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Bitmap?): Void {
            return bitmapToFile(params[0]!!)!!
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            progressBar.visibility = View.INVISIBLE
            Toast.makeText(this@EditActivity, "YEET", Toast.LENGTH_SHORT).show()
        }

        fun bitmapToFile(bitmap: Bitmap): Void? {

            val path = Environment.getExternalStorageDirectory().toString()
            val file = File(path, "dupa.jpg")

            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            stream.flush()
            stream.close()

            return null
        }
    }

    // asynchronous class for heavy processing tasks
    internal inner class Processing : AsyncTask<Bitmap, Void, Bitmap>() {
        override fun onPreExecute() {
            super.onPreExecute()

            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Bitmap?): Bitmap? {
            return Editing().makeBlack(params[0]!!)
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
            fun makeBlack(bitmap: Bitmap): Bitmap {

                // create a mutable copy of the bitmap
                val processed = bitmap.copy(Bitmap.Config.ARGB_8888, true)

                // check every single pixel
                for (y in 0 until bitmap.height) {
                    for (x in 0 until bitmap.width) {
                        val currentPixel = bitmap.getPixel(x, y)

                        val red = Color.red(currentPixel)
                        val green = Color.green(currentPixel)
                        val blue = Color.blue(currentPixel)

                        if (red + green + blue <= 140) {
                            processed.setPixel(x, y, Color.rgb(0, 0, 0))
                        }
                    }
                }

                return processed
            }
        }
    }
}

