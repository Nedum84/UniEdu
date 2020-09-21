package com.uniedu.ui.fragment.bottomsheet

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.uniedu.R
import com.uniedu.adapter.AdapterAnswer
import com.uniedu.adapter.AnswerClickListener
import com.uniedu.databinding.FragmentAnswersBinding
import com.uniedu.model.Answers
import com.uniedu.model.Questions
import com.uniedu.room.DatabaseRoom
import com.uniedu.ui.fragment.BaseFragmentBottomSheet
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.viewmodel.ModelAnswersFrag


private const val QUESTION = "question"


class FragmentAnswer : BaseFragmentBottomSheet() {
    private var question: Questions? = null
    lateinit var binding: FragmentAnswersBinding
    lateinit var fAnsModel: ModelAnswersFrag
    lateinit var databaseRoom:DatabaseRoom
    lateinit var answers: List<Answers>

    lateinit var ADAPTER:AdapterAnswer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        question = arguments?.getParcelable(QUESTION)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_answers, container, false)

        val application = requireNotNull(this.activity).application
        val viewModelFactory = ModelAnswersFrag.Factory(question!!, application)
        fAnsModel = ViewModelProvider(this, viewModelFactory).get(ModelAnswersFrag::class.java)

        databaseRoom = DatabaseRoom.getDatabaseInstance(application)
        binding.apply {
            lifecycleOwner = this@FragmentAnswer
            viewModel = fAnsModel
        }

//        binding.answerQuestion.setOnClickListener {
//            requireActivity().let {
//                FragmentAnswerQuestion.newInstance(question!!).apply {
//                    show(it.supportFragmentManager, tag)
//                }
//            }
//        }
        binding.answerQuestion.setOnClickListener {
            requireActivity().let {
                FragmentEBookDetail().apply {
                    show(it.supportFragmentManager, tag)
                }
            }
        }


        ADAPTER = AdapterAnswer(AnswerClickListener {
            ClassAlertDialog(application).toast("${it.answer_body}...!!!")
        })
        binding.recyclerAnswers.apply {
            adapter = ADAPTER
            layoutManager= LinearLayoutManager(activity)
            itemAnimator = DefaultItemAnimator()
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


    //FULL SCREEN DIALOG
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.peekHeight = PEEK_HEIGHT_AUTO
//            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels-200
        }
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