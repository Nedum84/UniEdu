package com.uniedu.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.uniedu.R
import com.uniedu.databinding.FragmentHomeBinding
import com.uniedu.ui.fragment.bottomsheet.FragmentAsk
import com.uniedu.ui.fragment.bottomsheet.FragmentChooseSchool
import com.uniedu.ui.fragment.bottomsheet.FragmentSellItem
import com.uniedu.viewmodel.HomeViewModel


class FragmentHome : BaseFragment() {
    lateinit var binding: FragmentHomeBinding

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        bindEvents()

        return binding.root
    }

    private fun bindEvents() {

        binding.schoolSelections.setOnClickListener {
            requireActivity().let {
                FragmentChooseSchool().apply {
//                    show(it.supportFragmentManager, tag)
                }
            }
        }

        binding.askQuestion.setOnClickListener {
            requireActivity().let {
                FragmentAsk().apply {
                    show(it.supportFragmentManager, tag)
                }
            }
        }
        binding.answerQuestion.setOnClickListener {
            requireActivity().let {
                FragmentSellItem().apply {
                    show(it.supportFragmentManager, tag)
                }
            }
        }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val vFactory = HomeViewModel.Factory(requireActivity().application, prefs.getCurUserDetail())
        homeViewModel = ViewModelProvider(this, vFactory).get(HomeViewModel::class.java)

        homeViewModel.school.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { school->
                homeViewModel.changeSchool(school)
            }
        })

        binding.root
    }
}