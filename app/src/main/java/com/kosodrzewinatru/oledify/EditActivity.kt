package com.kosodrzewinatru.oledify

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*
import android.util.Log
import android.view.View
import android.widget.Toast

class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // set imageView src via URI from MainActivity
        val selectedFileEdit = Uri.parse(intent.getStringExtra("selectedFileEdit"))
        imageEditView.setImageURI(selectedFileEdit)

        // get Bitmap from URI
        val bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedFileEdit)

        process.setOnClickListener {
            imageEditView.setImageBitmap(Editing.makeBlack(bitmap))
//            Async().execute(bitmap)
        }
    }

    // asynchronous class for heavy processing tasks
    internal inner class Async : AsyncTask<Bitmap, Void, Bitmap>() {
        override fun onPreExecute() {
            super.onPreExecute()

            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Bitmap?): Bitmap? {
            return Editing.makeBlack(params[0]!!)
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)

            progressBar.visibility = View.INVISIBLE
        }
    }
}

object Editing : AppCompatActivity() {

    // main function responsible for processing bitmap
    fun makeBlack(bitmap: Bitmap): Bitmap {

        // create a mutable copy of the bitmap
        val processed = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // check every single pixel
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val red = Color.red(bitmap.getPixel(x, y))
                val green = Color.green(bitmap.getPixel(x, y))
                val blue = Color.blue(bitmap.getPixel(x, y))

                if (red + green + blue <= 140) {
                    processed.setPixel(x, y, Color.rgb(0, 0, 0))
                }
            }
        }

        return processed
    }

    // check if pixel is closer to black
    private fun isCloserToBlack(bitmap: Bitmap, x: Int, y: Int): Boolean {

        val gamma = 2.2

        // rgb values of two points
        val redX = Color.red(bitmap.getPixel(x, y))
        val greenX = Color.green(bitmap.getPixel(x, y))
        val blueX = Color.blue(bitmap.getPixel(x, y))

        // get sum of rgb in 0.0-1.0 range
        val sumX = 0.2126 * Math.pow(redX.toDouble(), gamma) + 0.7152 * Math.pow(greenX.toDouble(), gamma)+ 0.0722 * Math.pow(blueX.toDouble(), gamma)

        return sumX <= Math.pow(0.5, gamma)
    }
}
