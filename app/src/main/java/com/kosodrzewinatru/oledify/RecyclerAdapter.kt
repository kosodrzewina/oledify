package com.kosodrzewinatru.oledify

import ImagePreviewFragment
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.gallery_item.view.*

class RecyclerAdapter(
    private val itemList: MutableList<GalleryItem>,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>() {

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.gallery_item,
            parent, false
        )

        return RecyclerViewHolder(itemView, context)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = itemList[position]
        val bitmap = BitmapFactory.decodeFile(currentItem.path)

        holder.imageView.setImageBitmap(bitmap)
        holder.imageView.tag = currentItem.path
    }

    fun addNewItem(galleryItem: GalleryItem) {
        itemList.add(galleryItem)
    }

    fun removeItem(path: String) {
        for (i in 0..itemList.size) {
            if (itemList[i].path == path) {
                itemList.removeAt(i)
                notifyItemRemoved(i)
                notifyItemRangeChanged(i, itemCount - i)
                break
            }
        }
    }

    fun removeAllItems() {
        val size = itemList.size
        itemList.clear()
        notifyItemRangeRemoved(0, size)
    }

    class RecyclerViewHolder(itemView: View, private val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image_view

        init {
            itemView.setOnClickListener {
                val imagePreviewFragment = ImagePreviewFragment(imageView.tag as String)
                imagePreviewFragment.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    "PREVIEW"
                )
            }
        }
    }
}