package com.kosodrzewinatru.oledify.fragments
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kosodrzewinatru.oledify.GalleryItem
import com.kosodrzewinatru.oledify.R
import java.io.File

class GalleryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val imagesPath =
            Environment.getExternalStorageDirectory().toString() + "/Pictures/Oledify"
        val files = File(imagesPath).listFiles().toList()
        val galleryItems = mutableListOf<GalleryItem>()

        for (file in files) {
            val currentBitmap = BitmapFactory.decodeFile(file.path)

            galleryItems.add(GalleryItem(currentBitmap))
        }

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }
}