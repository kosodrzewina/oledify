package com.kosodrzewinatru.oledify.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.kosodrzewinatru.oledify.ImplementStates
import com.kosodrzewinatru.oledify.R
import com.kosodrzewinatru.oledify.fragments.GalleryFragment
import com.kosodrzewinatru.oledify.fragments.LanguagesFragment
import com.kosodrzewinatru.oledify.fragments.WelcomeFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = "MainActivity"
    }

    private lateinit var selectedFileEdit: String

    private lateinit var bottomNav: BottomNavigationView

    // fragments
    private val languagesFragment = LanguagesFragment()
    private val welcomeFragment = WelcomeFragment()
    private val galleryFragment = GalleryFragment()

    private val sharedPrefs = "sharedPrefs"
    private val IS_FIRST_LAUNCH = "isFirstLaunch"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        // things set on the first launch
        if (getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
                .getBoolean(IS_FIRST_LAUNCH, true)
        ) {
            //welcome fragment
            welcomeFragment.show(supportFragmentManager, "WELCOME")
            getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(IS_FIRST_LAUNCH, false)
                .apply()
        }

        bottomNav = findViewById(R.id.bottom_nav_main)

        magic_button.isEnabled = false
        clear_button.isEnabled = false

        toolbar_main.title = getString(R.string.app_name)

        // open file picker
        upload_button.setOnClickListener {
            val intent = Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(
                Intent.createChooser(
                    intent,
                    "Select a file"
                ),
                100
            )
        }

        // go to EditActivity
        magic_button.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("selectedFileEdit", selectedFileEdit)
            startActivity(intent)
        }

        image_preview_view.setImageDrawable(resources.getDrawable(R.drawable.ic_splash, null))

        // clear imageView
        clear_button.setOnClickListener {
            image_preview_view.setImageDrawable(resources.getDrawable(R.drawable.ic_splash, null))
            magic_button.isEnabled = false
            clear_button.isEnabled = false
        }

        ImplementStates().languageState(this)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav_main)

        // back-end for stuff in the bottom navigation view
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.switch_editing -> {
                    if (galleryFragment.isVisible) {
                        supportFragmentManager.beginTransaction().remove(galleryFragment).commit()
                        upload_button.show()
                    }
                }

                R.id.switch_gallery -> {
                    upload_button.hide()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_main, galleryFragment).commit()
                }

                R.id.settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }

            return@setOnNavigationItemSelectedListener true
        }
    }

    // if back button is pressed and the drawer is open, close the drawer
//    override fun onBackPressed() {
//        when {
//            drawerMain.isDrawerOpen(GravityCompat.START) -> drawerMain.closeDrawer(GravityCompat.START)
//            galleryFragment.isVisible -> {
//                supportFragmentManager.beginTransaction().remove(galleryFragment).commit()
//                uploadButton.show()
//                navViewMain.menu.getItem(0).isChecked = true
//            }
//            else -> super.onBackPressed()
//        }
//    }

    // result of quitting file picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val selectedFile = data?.data

            // passing URI to EditActivity
            selectedFileEdit = selectedFile.toString()

            // image showed via URI
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedFile)
            val thumbnail = Bitmap.createScaledBitmap(
                bitmap,
                bitmap.width / 2,
                bitmap.height / 2,
                true
            )

            image_preview_view.setImageBitmap(thumbnail)

            magic_button.isEnabled = true
            clear_button.isEnabled = true
        }
    }
}
