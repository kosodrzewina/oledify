package com.kosodrzewinatru.oledify

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*
import android.util.Log

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

    // check if pixel is contrasting
    fun isContrasting(bitmap: Bitmap, x: Int, y: Int, x1: Int, y1: Int): Boolean {

        // combined values of two points
        val sumX = Color.red(bitmap.getPixel(x, y)) + Color.green(bitmap.getPixel(x, y)) + Color.blue(bitmap.getPixel(x, y))
        val sumX1 = Color.red(bitmap.getPixel(x1, y1)) + Color.green(bitmap.getPixel(x1, y1)) + Color.blue(bitmap.getPixel(x1, y1))

        var difference: Int

        if (sumX >= sumX1) {
            difference = sumX - sumX1
        }
        else {
            difference = sumX1 - sumX
        }

        // temporary
        return false
    }
}
