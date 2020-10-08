package com.uniedu.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.uniedu.R
import com.uniedu.adapter.AdapterEBooks
import com.uniedu.adapter.AdapterQuestion
import com.uniedu.adapter.EBookClickListener
import com.uniedu.adapter.QuestionClickListener
import com.uniedu.databinding.FragmentEBookBinding
import com.uniedu.model.EBooks
import com.uniedu.model.Questions
import com.uniedu.room.DatabaseRoom
import com.uniedu.ui.fragment.bottomsheet.FragmentAnswer
import com.uniedu.ui.fragment.bottomsheet.FragmentEBookDetail
import com.uniedu.ui.fragment.bottomsheet.FragmentUploadEBook
import com.uniedu.utils.ClassUtilities
import com.uniedu.viewmodel.ModelCourses
import com.uniedu.viewmodel.ModelEbook
import com.uniedu.viewmodel.ModelQuestionsFrag
import kotlinx.coroutines.launch

class FragmentEBook : BaseFragment() {
    lateinit var binding:FragmentEBookBinding
    lateinit var modelEbook: ModelEbook
    lateinit var ADAPTER: AdapterEBooks

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = ModelEbook.Factory(application)
        modelEbook = requireActivity().run{
            ViewModelProvider(this, viewModelFactory).get(ModelEbook::class.java)
        }


        modelEbook.ebooks().observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.isEmpty()&&ADAPTER.itemCount==0){
//                    binding.noComplainWrapper.visibility = View.VISIBLE
                }else{
//                    binding.noComplainWrapper.visibility = View.GONE

                    ADAPTER.list =it
                }
            }
        })
        modelEbook.feedBack.observe(viewLifecycleOwner, Observer {
            when(it){
                "network_success"->{}
                "network_error"->{
//                    if (questions.isEmpty()){
//                        //show no network tag
//                    }
                }
            }
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_e_book, container, false)

        initToolbar()
        bindActions()

        binding.apply {
            lifecycleOwner = this@FragmentEBook
        }


        ADAPTER = AdapterEBooks(EBookClickListener{
            val frag = FragmentEBookDetail.newInstance(it)
            requireActivity().let {
                frag.apply {
                    show(it.supportFragmentManager, tag)
                }
            }
        })
        binding.recyclerEbook.apply {
            adapter = ADAPTER
            layoutManager= GridLayoutManager(activity,  ClassUtilities().calculateNoOfColumns(thisContext,126f))
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = false
        }


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

    private fun initToolbar() {
        binding.toolbar.inflateMenu(R.menu.menu_question)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.action_menu_filter_question->{

                }
                else -> super.onOptionsItemSelected(it)
            }

            true
        }
    }

}