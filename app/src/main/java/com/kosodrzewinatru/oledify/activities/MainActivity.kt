package com.kosodrzewinatru.oledify.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.preference.PreferenceManager
import com.kosodrzewinatru.oledify.ImplementStates
import com.kosodrzewinatru.oledify.R
import com.kosodrzewinatru.oledify.fragments.AboutFragment
import com.kosodrzewinatru.oledify.fragments.GalleryFragment
import com.kosodrzewinatru.oledify.fragments.LoadingFragment
import com.kosodrzewinatru.oledify.fragments.WelcomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_loading.*


class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = "MainActivity"
    }

    private lateinit var selectedFileEdit: String

    // fragments
    private val welcomeFragment = WelcomeFragment()
    private val galleryFragment = GalleryFragment()
    private val aboutFragment = AboutFragment()
    private val loadingFragment = LoadingFragment()

    private val sharedPrefs = "sharedPrefs"
    private val IS_FIRST_LAUNCH = "isFirstLaunch"

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ImplementStates().themeState(this)

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

        magic_button.isEnabled = false
        clear_button.isEnabled = false

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

            switchButtonState(magic_button, false)
            switchButtonState(clear_button, false)
        }

        // back-end for stuff in the bottom navigation view
        bottom_nav_main.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.switch_editing -> {
                    if (galleryFragment.isVisible) {
                        supportFragmentManager.beginTransaction().remove(galleryFragment).commit()
                        upload_button.show()
                    }
                }

                R.id.switch_gallery -> {
                    if (!galleryFragment.isVisible) {
                        galleryFragment.manageData(loadingFragment)
                        loadingFragment.manageData(resources.getString(R.string.gallery_loading))
                        loadingFragment.show(supportFragmentManager, "LOADING_START")

                        upload_button.hide()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_main, galleryFragment).commit()
                    }
                }
            }

            return@setOnNavigationItemSelectedListener true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings_item -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)

                true
            }

            R.id.about_item -> {
                aboutFragment.show(supportFragmentManager, "ABOUT")

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (galleryFragment.isVisible) {
            supportFragmentManager.beginTransaction().remove(galleryFragment).commit()
            upload_button.show()
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

            val bitmap: Bitmap

            // image showed via URI
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, selectedFile ?: return)
                bitmap = ImageDecoder.decodeBitmap(source)
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedFile)
            }

            val thumbnail = Bitmap.createScaledBitmap(
                bitmap,
                bitmap.width / 2,
                bitmap.height / 2,
                true
            )

            image_preview_view.setImageBitmap(thumbnail)

            switchButtonState(magic_button, true)
            switchButtonState(clear_button, true)
        }
    }

    private fun switchButtonState(button: AppCompatButton, state: Boolean) {
        if (state) {
            button.isEnabled = true
            button.setBackgroundResource(R.drawable.button_enabled)
            button.setTextColor(Color.parseColor("#212121"))
        } else {
            button.isEnabled = false
            button.setBackgroundResource(R.drawable.button_disabled)
            button.setTextColor(Color.parseColor("#616161"))
        }
    }
}
