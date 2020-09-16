package com.uniedu.ui.fragment.bottomsheet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.uniedu.utils.ClassSharedPreferences
import com.ng.rapetracker.viewmodel.factory.GetRapeDetailViewModelFactory
import com.uniedu.R
import com.uniedu.adapter.AdapterAnswer
import com.uniedu.adapter.AnswerClickListener
import com.uniedu.databinding.FragmentAnswersBinding
import com.uniedu.model.Answers
import com.uniedu.model.Questions
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.viewmodel.ModelAnswersFrag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val QUESTION = "question"


class FragmentAnswer : Fragment() {
    private var question: Questions? = null
    lateinit var binding: FragmentAnswersBinding
    lateinit var fAnsModel: ModelAnswersFrag
    lateinit var prefs:ClassSharedPreferences
    lateinit var databaseRoom:DatabaseRoom
    lateinit var answers: List<Answers>

    lateinit var thisContext:Activity
    lateinit var ADAPTER:AdapterAnswer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        question = arguments?.getParcelable(QUESTION)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_answers, container, false)
        thisContext = requireActivity()

        val application = requireNotNull(this.activity).application
        val viewModelFactory = ModelAnswersFrag.Factory(question!!, application)
        fAnsModel = ViewModelProvider(this, viewModelFactory).get(ModelAnswersFrag::class.java)
        prefs = ClassSharedPreferences(thisContext)

        databaseRoom = DatabaseRoom.getDatabaseInstance(application)
        binding.apply {
            lifecycleOwner = this@FragmentAnswer
            viewModel = fAnsModel
        }



        ADAPTER = AdapterAnswer(AnswerClickListener {
            ClassAlertDialog(application).toast("${it.answer_body}...!!!")
        })
        binding.recyclerAnswers.apply {
            adapter = ADAPTER
            layoutManager= LinearLayoutManager(activity)
        }

        fAnsModel.answers.observe(viewLifecycleOwner, Observer {
            it?.let {

                if (it.isEmpty()){
//                    binding.noComplainWrapper.visibility = View.VISIBLE
                }else{
//                    binding.noComplainWrapper.visibility = View.GONE

                    it?.apply {
                        ADAPTER?.list = it
                    }
                    answers = it
//                    activity?.let{act->
//                        act.runOnUiThread {
//                            ADAPTER?.list = it
//                        }
//                    }
                }
            }
        })
        fAnsModel.feedBack.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {fb->//feedback
                when(fb){
                    "network_success"->{}
                    "network_error"->{
                        if (answers.isEmpty()){
                            //show no network tag
                        }
                    }
                }
            }
        })



        return binding.root
    }



    companion object {

        @JvmStatic
        fun newInstance(param: Parcelable) =
            FragmentAnswer().apply {
                arguments = Bundle().apply {
                    putParcelable(QUESTION, param)
                }
            }
    }
}