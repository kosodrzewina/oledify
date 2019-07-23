package com.kosodrzewinatru.oledify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.SwitchCompat
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var selectedFileEdit: String

    private lateinit var drawer: DrawerLayout

    private val fragmentManager = supportFragmentManager
    private val languagesFragment = LanguagesFragment()

    val SHARED_PREFS = "sharedPrefs"
    val SWITCH_REAL_TIME = "switchRealTime"

    var switchRealTimeState = findViewById<SwitchCompat>(R.id.realTime).isChecked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawer = findViewById(R.id.drawerMain)
        val navigationView = findViewById<NavigationView>(R.id.navViewMain)
        navigationView.setNavigationItemSelectedListener(this)

        magicButton.isEnabled = false

        toolbarMain.title = getString(R.string.app_name)

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
        }
    }

    // saving values with shared preferences after closing the app
    private fun saveValues() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // state of real-time-switch in the drawer
        val switchRealTime = findViewById<SwitchCompat>(R.id.realTime)
        editor.putBoolean(SWITCH_REAL_TIME, switchRealTime.isChecked)

        editor.apply()
    }

    // loading values with shared preferences
    private fun loadValues() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

        // state of real-time-switch in the drawer
        switchRealTimeState = sharedPreferences.getBoolean(SWITCH_REAL_TIME, false)
    }

    // updating values with shared preferences
    private fun updateValues() {
        // updating state of real-time-switch
        val switchRealTime = findViewById<SwitchCompat>(R.id.realTime)
        switchRealTime.isChecked = switchRealTimeState
    }

    // back-end for stuff in the drawer
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId) {
            R.id.language -> languagesFragment.show(fragmentManager, "LIST")
        }

        return true
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
