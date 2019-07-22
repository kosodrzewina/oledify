package com.kosodrzewinatru.oledify

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView

class LanguagesFragment: DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.dialog_fragment_languages, container, false)

        var menuItems = arrayOf<String>("Item_1", "Item_2", "Item_3")

        val listView = view?.findViewById<ListView>(R.id.listLanguages)

        var listViewAdapter = ArrayAdapter<String>(
            activity,
            android.R.layout.simple_list_item_1,
            menuItems
        )

        listView?.adapter = listViewAdapter

        return view
    }
}