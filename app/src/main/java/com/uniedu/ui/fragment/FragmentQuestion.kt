package com.uniedu.ui.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.uniedu.R
import com.uniedu.adapter.AdapterQuestion
import com.uniedu.adapter.QuestionClickListener
import com.uniedu.databinding.FragmentQuestionBinding
import com.uniedu.model.Questions
import com.uniedu.room.DatabaseRoom
import com.uniedu.ui.fragment.bottomsheet.FragmentAnswer
import com.uniedu.ui.fragment.bottomsheet.FragmentAnswerQuestion
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.utils.ClassSharedPreferences
import com.uniedu.viewmodel.ModelQuestionsFrag
import kotlinx.coroutines.launch


class FragmentQuestion : BaseFragment() {
    lateinit var binding: FragmentQuestionBinding

    lateinit var modelQuestionsFrag: ModelQuestionsFrag
    lateinit var databaseRoom: DatabaseRoom

    lateinit var ADAPTER: AdapterQuestion
    lateinit var questions: List<Questions>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_question, container, false)

        initToolbar()
        val application = requireNotNull(this.activity).application
        val viewModelFactory = ModelQuestionsFrag.Factory(application)
        modelQuestionsFrag = ViewModelProvider(this, viewModelFactory).get(ModelQuestionsFrag::class.java)

        databaseRoom = DatabaseRoom.getDatabaseInstance(application)
        binding.apply {
            lifecycleOwner = this@FragmentQuestion
            viewModelQuestion = modelQuestionsFrag
        }


        ADAPTER = AdapterQuestion(application, QuestionClickListener {
            val fragAns = FragmentAnswer.newInstance(it)
            requireActivity().let {
                fragAns.apply {
                    show(it.supportFragmentManager, tag)
                }
            }
        })
        binding.recyclerQuestion.apply {
            adapter = ADAPTER
            layoutManager= LinearLayoutManager(activity)
            itemAnimator = DefaultItemAnimator()
        }

        modelQuestionsFrag.questions.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.isEmpty()){
//                    binding.noComplainWrapper.visibility = View.VISIBLE
                }else{
//                    binding.noComplainWrapper.visibility = View.GONE

                    questions = it
                    activity?.let{act->
                        act.runOnUiThread {
                            launch {
                                ADAPTER.addNewItems(it)
                            }
                        }
                    }
                }
            }
        })
        modelQuestionsFrag.feedBack.observe(viewLifecycleOwner, Observer {
            when(it){
                "network_success"->{}
                "network_error"->{
//                    if (questions.isEmpty()){
//                        //show no network tag
//                    }
                }
            }
        })

        modelQuestionsFrag.qSearchParam.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled().let {
                it?.let {

                }
            }
        })


        return binding.root
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