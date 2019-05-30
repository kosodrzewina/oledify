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

        val bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedFileEdit)

        process.setOnClickListener {
            Editing.makeBlack(bitmap)
        }
    }
}

object Editing : AppCompatActivity() {

    fun makeBlack(bitmap: Bitmap): Bitmap {
        val pixels = IntArray(bitmap.width * bitmap.height)
//        bitmap.getPixels(pixels, 0, 0, 0, 0, bitmap.width, bitmap.height)

        val pixel = bitmap.getPixel(3, 7)

        Log.d("red", Color.red(pixel).toString())
        Log.d("green", Color.green(pixel).toString())
        Log.d("blue", Color.blue(pixel).toString())
        
        return bitmap
    }
}
