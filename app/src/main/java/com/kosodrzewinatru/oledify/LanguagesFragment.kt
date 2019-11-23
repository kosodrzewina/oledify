package com.kosodrzewinatru.oledify

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import java.util.*

class LanguagesFragment : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_fragment_languages, container, false)

        val supportedLocales = arrayOf(Locale.ENGLISH, Locale.FRENCH, Locale("pl", "PL"))
        val menuItems = arrayListOf<String>()

        (supportedLocales.indices).forEach {
            menuItems.add(supportedLocales[it].displayLanguage)
        }

        val listView = view?.findViewById<ListView>(R.id.listLanguages)

        val listViewAdapter = ArrayAdapter<String>(
            activity,
            android.R.layout.simple_list_item_1,
            menuItems
        )

        listView?.adapter = listViewAdapter

        listView?.setOnItemClickListener { _, _, i, _ ->
            (activity as MainActivity).changeLanguage(supportedLocales[i])
        }

        return view
    }
}