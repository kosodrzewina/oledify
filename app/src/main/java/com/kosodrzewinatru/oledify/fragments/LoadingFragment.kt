package com.kosodrzewinatru.oledify.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kosodrzewinatru.oledify.DataMover
import com.kosodrzewinatru.oledify.R
import kotlinx.android.synthetic.main.fragment_loading.*

class LoadingFragment: DialogFragment(), DataMover<String> {
    private lateinit var loadingText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_loading, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading_text.text = loadingText
    }

    override fun manageData(data: String) {
        loadingText = data
    }
}
