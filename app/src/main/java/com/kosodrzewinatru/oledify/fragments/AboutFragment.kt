package com.kosodrzewinatru.oledify.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kosodrzewinatru.oledify.R
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val packageInfo = context?.packageManager?.getPackageInfo(context?.packageName, 0)
        val versionName = packageInfo?.versionName

        app_version.text = "${getString(R.string.version)}: $versionName"
        licence.text = "- PhotoView"
    }
}