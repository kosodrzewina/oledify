package com.kosodrzewinatru.oledify.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kosodrzewinatru.oledify.GalleryItem
import com.kosodrzewinatru.oledify.R
import com.kosodrzewinatru.oledify.RecyclerAdapter
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.io.File

class GalleryFragment : Fragment() {
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
        } else {
            showGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 101)
            showGallery()
    }

    fun showGallery() {
        val directory =
            File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Oledify")

        if (directory.isDirectory &&
            directory.listFiles() != null &&
            directory.listFiles().isNotEmpty()
        ) {
            val files = directory.listFiles().toList()
            val galleryItems = mutableListOf<GalleryItem>()

            files.indices.forEach {
                val bitmap = BitmapFactory.decodeFile(files[it].path)
                galleryItems.add(GalleryItem(bitmap))
            }

            images_recycler_view.adapter = RecyclerAdapter(galleryItems)
            images_recycler_view.layoutManager = LinearLayoutManager(activity)
            images_recycler_view.setHasFixedSize(true)
        }
    }
}