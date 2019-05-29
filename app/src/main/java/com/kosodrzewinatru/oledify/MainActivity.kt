package com.kosodrzewinatru.oledify

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var selectedFileEdit: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        magicButton.isEnabled = false

        // open file picker
        uploadButton.setOnClickListener {
            val intent= Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 100)
        }

        // go to EditActivity
        magicButton.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("selectedFileEdit", selectedFileEdit)
            startActivity(intent)
        }
    }

    // result of quitting file picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val selectedFile = data?.data

            // passing URI to EditActivity
            selectedFileEdit = selectedFile.toString()

            // image showed via URI
            imagePreviewView.setImageURI(selectedFile)

            magicButton.isEnabled = true
        }
    }
}
