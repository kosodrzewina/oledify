package com.kosodrzewinatru.oledify.fragments

import android.graphics.Bitmap
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
        val directory =
            File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Oledify")

        if (directory.isDirectory &&
            directory.listFiles() != null &&
            directory.listFiles().isNotEmpty()
        ) {
            val files = directory.listFiles().toList()
            val galleryItems = mutableListOf<GalleryItem>()

            var previousBitmap = BitmapFactory.decodeFile(files[0].path)
            files.indices.forEach {
                val currentBitmap = BitmapFactory.decodeFile(files[it].path)

                if (it % 2 != 0)
                    galleryItems.add(GalleryItem(previousBitmap, currentBitmap))
                else if (it != 0)
                    previousBitmap = BitmapFactory.decodeFile(files[it].path)
            }

            imagesRecyclerView.adapter = RecyclerAdapter(galleryItems)
            imagesRecyclerView.layoutManager = LinearLayoutManager(activity)
            imagesRecyclerView.setHasFixedSize(true)
        }
    }
}