package com.uniedu.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.skydoves.powermenu.PowerMenu
import com.uniedu.R
import com.uniedu.databinding.ActivityMainBinding
import com.uniedu.databinding.FragmentHomeBinding
import com.uniedu.ui.fragment.bottomsheet.FragmentAsk
import com.uniedu.ui.fragment.bottomsheet.FragmentChooseSchool
import com.uniedu.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*


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