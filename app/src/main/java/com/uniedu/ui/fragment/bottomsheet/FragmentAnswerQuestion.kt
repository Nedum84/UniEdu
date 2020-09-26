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
import com.uniedu.databinding.FragmentAnswerQuestionBinding
import com.uniedu.databinding.FragmentEBookDetailBinding
import com.uniedu.extension.*
import com.uniedu.model.Answers
import com.uniedu.model.Questions
import com.uniedu.network.RetrofitPOST
import com.uniedu.network.ServerResponse
import com.uniedu.network.UploadImage
import com.uniedu.room.DatabaseRoom
import com.uniedu.ui.fragment.BaseFragmentBottomSheet
import com.uniedu.ui.fragment.BaseFragmentBottomSheetUploadFile
import com.uniedu.utils.ClassProgressDialog
import com.uniedu.viewmodel.ModelAnswersFrag
import com.uniedu.viewmodel.ModelUploadFile
import jp.wasabeef.richeditor.RichEditor
import kotlinx.android.synthetic.main.fragment_ask.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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


class FragmentAnswerQuestion : BaseFragmentBottomSheetUploadFile() {
    lateinit var fAnsModel: ModelAnswersFrag
    private var question: Questions? = null
    private var answer: Answers? = null
    var is_adding_new = true

    lateinit var binding:FragmentAnswerQuestionBinding

    private lateinit var mEditor: RichEditor
    private lateinit var mPreview: TextView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = ModelAnswersFrag.Factory(question!!, requireActivity().application)
        fAnsModel = requireActivity().run{
            ViewModelProvider(this, viewModelFactory).get(ModelAnswersFrag::class.java)
        }

        modelUploadFile = requireActivity().run {
            ViewModelProvider(this).get(ModelUploadFile::class.java)
        }
        modelUploadFile.imageUploaded.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.getContentIfNotHandled()?.let {content->
                publishContent(content)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        arguments?.let {
            question = it.getParcelable(QUESTION)
            answer = it.getParcelable(ANSWER)

            if (answer!=null){
                is_adding_new = false
                launch {
                    withContext(Dispatchers.Main){
                        mEditor.html = question?.question_body
                    }
                }
            }else{
                is_adding_new = true
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_answer_question, container, false)
        // Inflate the layout for this fragment

        launch {
            withContext(Dispatchers.Main){
                initToolbar(binding.root)
                bindActions()
                mEditor = binding.editor
                binding.root.setupTextEditor(mEditor)
            }
        }

        return binding.root
    }

    private fun bindActions() {
        binding.publish.setOnClickListener {
            if (mEditor.html.isNullOrEmpty()){
                thisContext.toast("Enter your answer...")
                mEditor.focusEditor()
            }else{
                pDialog?.createDialog()
                val imgPaths = mEditor.html.getImgPaths()
                if (imgPaths.isEmpty()){
                    publishContent()
                }else{
                    uploadImage(imgPaths)
                }
            }
        }

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


    private fun publishContent(content:String = mEditor.html) {
        val retrofit = RetrofitPOST.retrofitWithJsonResponse.create(AddAnswer::class.java)
        retrofit.addAnswer(
            "add_answer",
            content,
            question!!.question_id,
            "${answer?.answer_id}",
            myDetails.user_id,
            is_adding_new
        ).enqueue(object :Callback<ServerResponse>{
            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                pDialog?.dismissDialog()
                requireContext().toast("No internet connect!")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<ServerResponse>,response: Response<ServerResponse>) {
                pDialog?.dismissDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {

                        val serverResponse = response.body()

                        if (!(serverResponse!!.success as Boolean)){
                            context!!.toast(serverResponse.respMessage!!)
                        }else{
                            thisContext.toast(if (is_adding_new) "Answer published" else "Answer updated")
                            val  item:Answers  = Answers(0,"",""
                                ,"","","","",false,"","","")
                            try {
                                val answers = serverResponse.otherDetail?.toListModel(item)!!
                                fAnsModel.addToDb(answers)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            published()
                        }
                    }
                } else {
                    context!!.toast("An error occurred, Try again")
                }
            }

        })
    }












    //Make full screen
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = context?.makeFullScreen(this)
        return dialog!!
    }


    companion object {

        @JvmStatic
        fun newInstance(question: Parcelable, answer: Parcelable? = null) =
            FragmentAnswerQuestion().apply {
                arguments = Bundle().apply {
                    putParcelable(QUESTION, question)
                    putParcelable(ANSWER, answer)
                }
            }
    }
}

interface AddAnswer{
    @Multipart
    @POST("add_answer.php")
    fun addAnswer(
        @Part("request_type") request_type:String,
        @Part("answer_body") answer_body: String,
        @Part("question_id") question_id:Int,
        @Part("answer_id") answer_id:String? = null,
        @Part("answer_from") answer_from:Int,
        @Part("is_adding_new") is_adding_new:Boolean
    ):Call<ServerResponse>
}
