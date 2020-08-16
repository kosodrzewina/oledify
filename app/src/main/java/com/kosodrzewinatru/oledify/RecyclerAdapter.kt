package com.kosodrzewinatru.oledify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.gallery_item.view.*

class RecyclerAdapter(private val itemList: List<GalleryItem>) :
    RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>() {

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.gallery_item,
            parent, false
        )

        return RecyclerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.photoView0.setImageBitmap(currentItem.bitmap0)

        if (position < itemCount)
            holder.photoView1.setImageBitmap(currentItem.bitmap1)
    }

    class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoView0 = itemView.photo_view_0
        val photoView1 = itemView.photo_view_1
    }
}