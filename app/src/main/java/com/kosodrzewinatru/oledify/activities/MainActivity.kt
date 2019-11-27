package com.kosodrzewinatru.oledify.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.kosodrzewinatru.oledify.R
import com.kosodrzewinatru.oledify.fragments.ComingSoonFragment
import com.kosodrzewinatru.oledify.fragments.LanguagesFragment
import com.kosodrzewinatru.oledify.fragments.SettingsFragment
import com.kosodrzewinatru.oledify.fragments.WelcomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var selectedFileEdit: String

    private lateinit var drawer: DrawerLayout

    // fragments
    private val languagesFragment =
        LanguagesFragment()
    private val welcomeFragment =
        WelcomeFragment()
    private val comingSoonFragment =
        ComingSoonFragment()
    private val settingsFragment =
        SettingsFragment()

    private val sharedPrefs = "sharedPrefs"
    private val isFirstLaunch = "isFirstLaunch"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // things set on the first launch
        if (getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE).getBoolean(isFirstLaunch, true)) {
            //welcome fragment
            welcomeFragment.show(supportFragmentManager, "WELCOME")
            getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE).edit().putBoolean(isFirstLaunch, false).apply()
        }

        drawer = findViewById(R.id.drawerMain)
        val navigationView = findViewById<NavigationView>(R.id.navViewMain)
        navigationView.setNavigationItemSelectedListener(this)

        magicButton.isEnabled = false

        toolbarMain.title = getString(R.string.app_name)

        // hamburger icon
        val toggle = ActionBarDrawerToggle(
            this,
            drawerMain,
            toolbarMain,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
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

    // back-end for stuff in the drawer
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId) {
            R.id.language -> languagesFragment.show(supportFragmentManager, "LIST")
            R.id.switchGallery -> comingSoonFragment.show(supportFragmentManager, "FEATURE")
            R.id.processingSettings -> {
                supportFragmentManager.beginTransaction().replace(R.id.drawerMain, settingsFragment)
                    .addToBackStack(null).commit()
                drawerMain.closeDrawer(GravityCompat.START)
            }
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

    fun changeLanguage(locale: Locale) {
        val config = resources.configuration

        Locale.setDefault(locale)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        finish()
        startActivity(intent)
    }
}
