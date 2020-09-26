package com.kosodrzewinatru.oledify.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import com.kosodrzewinatru.oledify.ImplementStates
import com.kosodrzewinatru.oledify.R
import com.kosodrzewinatru.oledify.activities.SettingsActivity
import java.util.*

class LanguagesFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.dialog_fragment_languages,
            container,
            false
        )

        val supportedLocales = arrayOf(
            Locale.ENGLISH,
            Locale("pl", "PL")
        )
        val menuItems = arrayListOf<String>()

        supportedLocales.indices.forEach {
            menuItems.add(supportedLocales[it].displayLanguage)
        }

        val listView = view?.findViewById<ListView>(R.id.list_languages)

        val listViewAdapter = ArrayAdapter<String>(
            activity ?: return null,
            android.R.layout.simple_list_item_1,
            menuItems
        )

        listView?.adapter = listViewAdapter

        listView?.setOnItemClickListener { _, _, i, _ ->
            SettingsActivity().localeToSharedPreferences(context!!, supportedLocales[i])

            ImplementStates().changeLanguage(
                activity!!, activity!!.intent, resources,
                supportedLocales[i]
            )
        }

        return view
    }
}