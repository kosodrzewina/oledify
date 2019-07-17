package com.kosodrzewinatru.oledify

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var selectedFileEdit: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        magicButton.isEnabled = false

        toolbarMain.title = getString(R.string.app_name)

        val fragmentManager = supportFragmentManager
        val languagesFragment = LanguagesFragment()

        // hamburger icon
        val toggle = ActionBarDrawerToggle(this, drawerMain, toolbarMain, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerMain.addDrawerListener(toggle)
        toggle.syncState()

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

        // clear imageView
        clearButton.setOnClickListener {
            imagePreviewView.setImageDrawable(null)
            magicButton.isEnabled = false

            languagesFragment.show(fragmentManager, "LIST")
        }
    }

    // if back button is pressed and the drawer is open, close the drawer
    override fun onBackPressed() {
        if (drawerMain.isDrawerOpen(GravityCompat.START)) {
            drawerMain.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
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
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedFile)
            val thumbnail = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)

            imagePreviewView.setImageBitmap(thumbnail)

            magicButton.isEnabled = true
        }
    }
}
