package com.uniedu.ui.fragment.bottomsheet

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.uniedu.network.ServerResponse
import com.uniedu.R
import com.uniedu.databinding.FragmentAskBinding
import com.uniedu.extension.toast
import com.uniedu.model.Courses
import com.uniedu.model.Questions
import com.uniedu.network.AskQuestionService
import com.uniedu.network.RetrofitPOST
import com.uniedu.room.DatabaseRoom
import com.uniedu.ui.fragment.BaseFragmentUploadFile
import com.uniedu.viewmodel.ModelCourses
import com.uniedu.viewmodel.ModelQuestionsFrag
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.HashMap

private const val QUESTION = "question"

class FragmentAsk : BaseFragmentUploadFile() {
    private var question: Questions? = null
    lateinit var binding:FragmentAskBinding
    lateinit var databaseRoom: DatabaseRoom

    lateinit var modelQuestionsFrag: ModelQuestionsFrag

    private var course: Courses? = null
    private var is_adding_new_question = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            question = it.getParcelable(QUESTION)
            is_adding_new_question = false
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ask, container, false)
        databaseRoom = DatabaseRoom.getDatabaseInstance(thisContext)

        val app = requireNotNull(this.activity).application
        val mFactory = ModelQuestionsFrag.Factory(app)
        modelQuestionsFrag = ViewModelProvider(this, mFactory).get(ModelQuestionsFrag::class.java)

        bindEvents()

        return binding.root
    }

    private fun bindEvents() {
        binding.pickImage.setOnClickListener {
            imagePickerDialog()
        }
        binding.submitQBTN.setOnClickListener {
            submitQuestion()
        }

        val mFactory = ModelCourses.Factory(thisContext.application)
        val modelCourses = ViewModelProvider(this, mFactory).get(ModelCourses::class.java)
        modelCourses.curCourse.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled().let {c->
                binding.addQCourse.text = c?.course_code
                course = c
            }
        })
    }


    private fun submitQuestion(){
        val question_body = binding.questionBody.text.toString().trim()

        if (question_body.isEmpty() && imageFilePath.isNullOrEmpty()){
            context.let {it!!.toast("No question entered")}
        }else{
//            val myDetails = Gson().fromJson(prefs.getCurUserDetail(), MyDetails::class.java)
            val myDetails = prefs.getCurUserDetail()

            // Map is used to multipart the file using okhttp3.RequestBody
            val map = HashMap<String, RequestBody>()
            val imgFile = File(imageFilePath!!)
            // Parsing any Media type file
            val requestBody     = RequestBody.create(MediaType.parse("*/*"), imgFile)
            map["file\"; filename=\"" + imgFile.name + "\""] = requestBody


            val file2 = File("gifImgPath")
            val requestBodyImg2  = RequestBody.create(MediaType.parse("*/*"), file2)
            map["file2\"; filename=\"" + file2.name + "\""] = requestBodyImg2

            val imgMap = if (imageFilePath!!.isNotEmpty())  map else null

            val logComplainService = RetrofitPOST.retrofitWithJsonResponse.create(AskQuestionService::class.java)
            logComplainService.askQuestionRequest(
                "ask_question",
                myDetails,
                "",
                imgMap,
                "${course?.course_id}",
                is_adding_new_question
            ).enqueue(object: Callback<ServerResponse> {
                override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                    requireContext().toast("No internet connect!")
                }

                override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            val serverResponse = response.body()

                            if (!(serverResponse!!.success as Boolean)){
                                context!!.toast(serverResponse.respMessage!!)
                            }else{
                                context!!.toast("Complain logged Successfully...")

//                                Adding to DB
                                val listResult = serverResponse.otherDetail
//                                databaseRoom.questionsDao.upSert(listResult)
//                                OR
                                modelQuestionsFrag.refreshQuestion()
                            }
                        }
                    } else {
                        context!!.toast("An error occurred, Try again")
                    }
                }

            })
        }
    }



    companion object {

        @JvmStatic
        fun newInstance(param: Parcelable) =
            FragmentAsk().apply {
                arguments = Bundle().apply {
                    putParcelable(QUESTION, param)
                }
            }
    }
}