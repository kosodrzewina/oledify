package com.kosodrzewinatru.oledify.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.kosodrzewinatru.oledify.DataMover
import com.kosodrzewinatru.oledify.GalleryItem
import com.kosodrzewinatru.oledify.R
import com.kosodrzewinatru.oledify.RecyclerAdapter
import com.kosodrzewinatru.oledify.activities.SettingsActivity
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class GalleryFragment : Fragment(), DataMover<DialogFragment> {
    companion object {
        private const val TAG = "GALLERY_FRAGMENT"
        private lateinit var loadingFragment: DialogFragment
        lateinit var files: List<File>
        var galleryItems = mutableListOf<GalleryItem>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storagePermission =
            context?.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (storagePermission == PackageManager.PERMISSION_DENIED) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                101
            )

            loadingFragment.dismiss()
        } else {
            showGallery()

            CoroutineScope(Dispatchers.Main).launch {
                populateGallery()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 101 && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            } == PackageManager.PERMISSION_GRANTED) {
            showGallery()
            populateGallery()
        } else {
            loadingFragment.dismiss()
            Toast.makeText(context, resources.getString(R.string.no_permissions), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        showGallery()
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth)
                inSampleSize *= 2
        }

        return inSampleSize
    }

    private fun decodeSampledBitmapFromFile(
        file: File,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.path, this)

            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false

            BitmapFactory.decodeFile(file.path, this)
        }
    }

    private fun showGallery() {
        val directory =
            File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Oledify")

        if (directory.isDirectory &&
            directory.listFiles() != null &&
            directory.listFiles().isNotEmpty()
        ) {
            files = directory.listFiles().toList()
            val columnCount = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SettingsActivity.COLUMN_COUNT, "2")

            images_recycler_view.adapter = RecyclerAdapter(galleryItems)
            images_recycler_view.layoutManager = GridLayoutManager(activity, columnCount!!.toInt())
            images_recycler_view.setHasFixedSize(true)
        }
    }

    private fun populateGallery() {
        if (images_recycler_view.adapter != null) {
            (images_recycler_view.adapter as RecyclerAdapter).removeAllItems()

            files.forEach { file ->
                (images_recycler_view.adapter as RecyclerAdapter).addNewItem(
                    GalleryItem(
                        decodeSampledBitmapFromFile(
                            file,
                            500,
                            500
                        )
                    )
                )
            }

            Log.i(TAG, "populated")

            images_recycler_view.adapter?.notifyDataSetChanged()
            showGallery()
        }

        loadingFragment.dismiss()
    }

    override fun manageData(data: DialogFragment) {
        loadingFragment = data
    }
}
