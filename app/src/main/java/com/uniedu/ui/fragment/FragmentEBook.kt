package com.uniedu.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.uniedu.R
import com.uniedu.databinding.FragmentEBookBinding
import com.uniedu.ui.fragment.bottomsheet.FragmentUploadEBook

class FragmentEBook : BaseFragmentBottomSheet() {
    lateinit var binding:FragmentEBookBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_e_book, container, false)

        bindActions()

        return binding.root
    }

    private fun bindActions() {
        binding.uploadMaterial.setOnClickListener {
            requireActivity().let {
                FragmentUploadEBook.newInstance().apply {
                    show(it.supportFragmentManager, tag)
                }
            }
        }
    }


}