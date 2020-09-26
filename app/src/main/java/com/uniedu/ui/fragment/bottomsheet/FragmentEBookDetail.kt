package com.uniedu.ui.fragment.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.uniedu.R
import com.uniedu.databinding.FragmentEBookDetailBinding
import com.uniedu.extension.*
import com.uniedu.model.Answers
import com.uniedu.model.Questions
import com.uniedu.network.RetrofitPOST
import com.uniedu.network.ServerResponse
import com.uniedu.network.UploadImage
import com.uniedu.ui.fragment.BaseFragmentBottomSheet
import com.uniedu.utils.ClassProgressDialog
import com.uniedu.viewmodel.ModelAnswersFrag
import jp.wasabeef.richeditor.RichEditor
import kotlinx.android.synthetic.main.fragment_ask.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.util.*


private const val QUESTION = "question"
private const val ANSWER = "answer"


class FragmentEBookDetail : BaseFragmentBottomSheet() {
    lateinit var fAnsModel: ModelAnswersFrag
    private var question: Questions? = null
    private var answer: Answers? = null
    var pDialog: ClassProgressDialog? = null
    var is_adding_new = true

    lateinit var binding:FragmentEBookDetailBinding


    private lateinit var mEditor: RichEditor
    private lateinit var mPreview: TextView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = ModelAnswersFrag.Factory(question!!, requireActivity().application)
        fAnsModel = requireActivity().run{
            ViewModelProvider(this, viewModelFactory).get(ModelAnswersFrag::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        arguments?.let {
            question = it.getParcelable(QUESTION)
            answer = it.getParcelable(ANSWER)

            if (answer!=null){
                !is_adding_new
            }else{
                is_adding_new = true
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_e_book_detail, container, false)
        // Inflate the layout for this fragment

        mEditor = binding.editor
        binding.root.setupTextEditor(mEditor)

        pDialog = ClassProgressDialog(thisContext)
        bindActions()

        return binding.root
    }

    private fun bindActions() {

        //Question preview
        binding.preViewQuestion.setEditorFontSize(14)
        binding.preViewQuestion.setPadding(2, 2, 2, 2)
        binding.preViewQuestion.setInputEnabled(false)
        binding.preViewQuestion.html = "<b>Question:</b><br>"+question?.question_body

        binding.viewQuestion.setOnClickListener {

            //if the position is equals to the item position which is to be expanded
            if (binding.preViewQuestion.visibility == View.GONE) {
                val slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down)
                binding.preViewQuestion.visibility = View.VISIBLE
                binding.preViewQuestion.startAnimation(slideDown)
                binding.dropDownToggle.setImageResource(R.drawable.ic_baseline_expand_less)
                binding.hideViewQuestion.text = "hide question"
            }else{
                binding.preViewQuestion.visibility = View.GONE
                binding.dropDownToggle.setImageResource(R.drawable.ic_baseline_expand_more)
                binding.hideViewQuestion.text = "view question"
            }
        }

    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = context?.makeFullScreen(this)
        return dialog!!
    }


    companion object {

        @JvmStatic
        fun newInstance(question: Parcelable, answer: Parcelable? = null) =
            FragmentEBookDetail().apply {
                arguments = Bundle().apply {
                    putParcelable(QUESTION, question)
                    putParcelable(ANSWER, answer)
                }
            }
    }
}

