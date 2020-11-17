package com.kosodrzewinatru.oledify.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.kosodrzewinatru.oledify.R
import kotlinx.android.synthetic.main.fragment_gallery.*

class ColumnCountFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_column_count,
            container,
            false
        )

        val sizes = arrayOf("1", "2", "3", "4")
        val listView = view?.findViewById<ListView>(R.id.column_count_list)
        val listViewAdapter = ArrayAdapter(
            activity ?: return null,
            android.R.layout.simple_list_item_1,
            sizes
        )

        listView?.adapter = listViewAdapter

        listView?.setOnItemClickListener { _, _, i, _ ->
            images_recycler_view.layoutManager = GridLayoutManager(activity, i)
        }

        return view
    }
}