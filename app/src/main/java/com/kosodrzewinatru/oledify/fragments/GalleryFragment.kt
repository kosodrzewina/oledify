package com.kosodrzewinatru.oledify.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
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
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.gallery_menu, menu)
//        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.column_count_button) {
            // @TODO open fragment to change column count
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 101)
            showGallery()
    }

    private fun showGallery() {
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
            images_recycler_view.layoutManager = GridLayoutManager(activity, 2)
            images_recycler_view.setHasFixedSize(true)
        }
    }
}