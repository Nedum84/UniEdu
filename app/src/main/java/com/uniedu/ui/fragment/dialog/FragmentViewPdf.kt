package com.uniedu.ui.fragment.dialog

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.uniedu.R
import com.uniedu.databinding.FragmentViewPdfBinding
import com.uniedu.utils.ClassAlertDialog


private const val FILE_URL = "file_url"

class FragmentViewPdf : DialogFragment() {
    lateinit var binding:FragmentViewPdfBinding
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(FILE_URL)
        }

        //Full screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
        } else {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_NoActionBar)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_pdf, container,false)
        val webView = binding.pdfWebview
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)

        return binding.root
    }



    companion object {


        @JvmStatic
        fun newInstance(param1: String) =
            FragmentViewPdf().apply {
                arguments = Bundle().apply {
                    putString(FILE_URL, param1)
                }
            }
    }
}