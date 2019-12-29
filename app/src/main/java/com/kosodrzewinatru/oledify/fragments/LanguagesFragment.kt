package com.kosodrzewinatru.oledify.fragments

import android.content.Context
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.kosodrzewinatru.oledify.ImplementStates
import com.kosodrzewinatru.oledify.R
import com.kosodrzewinatru.oledify.activities.SettingsActivity
import java.util.*

class LanguagesFragment : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_fragment_languages, container, false)

        val supportedLocales = arrayOf(Locale.ENGLISH, Locale.FRENCH, Locale("pl", "PL"))
        val menuItems = arrayListOf<String>()

        supportedLocales.indices.forEach {
            menuItems.add(supportedLocales[it].displayLanguage)
            Log.d("sdfg", supportedLocales[it].toString())
        }

        val listView = view?.findViewById<ListView>(R.id.listLanguages)

        val listViewAdapter = ArrayAdapter<String>(
            activity,
            android.R.layout.simple_list_item_1,
            menuItems
        )

        listView?.adapter = listViewAdapter

        listView?.setOnItemClickListener { _, _, i, _ ->
            val editor = activity?.getSharedPreferences(SettingsActivity.LOCALE,
                Context.MODE_PRIVATE)?.edit()

            editor?.putString(SettingsActivity.LOCALE, supportedLocales[i].toString())
            editor?.apply()

            ImplementStates().changeLanguage(activity!!, activity!!.intent, resources,
                supportedLocales[i])
        }

        return view
    }
}