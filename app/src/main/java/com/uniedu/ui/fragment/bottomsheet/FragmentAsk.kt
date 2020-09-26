package com.uniedu.ui.fragment.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.uniedu.R
import com.uniedu.databinding.FragmentAskBinding
import com.uniedu.extension.*
import com.uniedu.model.Answers
import com.uniedu.model.Courses
import com.uniedu.model.Questions
import com.uniedu.network.RetrofitPOST
import com.uniedu.network.ServerResponse
import com.uniedu.room.DatabaseRoom
import com.uniedu.ui.fragment.BaseFragmentBottomSheetUploadFile
import com.uniedu.utils.ClassProgressDialog
import com.uniedu.utils.ClassUtilities
import com.uniedu.viewmodel.ModelCourses
import com.uniedu.viewmodel.ModelQuestionsFrag
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
import retrofit2.http.PartMap
import java.io.File
import java.util.*

private const val QUESTION = "question"

class FragmentAsk : BaseFragmentBottomSheetUploadFile() {
    private var question: Questions? = null
    lateinit var binding:FragmentAskBinding
    lateinit var databaseRoom: DatabaseRoom

    lateinit var modelQuestionsFrag: ModelQuestionsFrag

    private lateinit var mEditor: RichEditor
    private var course: Courses? = null
    private var is_adding_new_question = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            question = it.getParcelable(QUESTION)

            if (!question!!.question_body.isNullOrEmpty()){
                launch {
                    is_adding_new_question = false
                    withContext(Dispatchers.Main){
                        course = DatabaseRoom.getDatabaseInstance(requireActivity().application).coursesDao.getById(question!!.course_id.toInt())
                        mEditor.html = question?.question_body
                        binding.addQCourse.text = course?.course_code
                    }
                }
            }
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ask, container, false)

        initToolbar(binding.root)
        mEditor = binding.editor
        binding.root.setupTextEditor(mEditor)

        bindActions()

        return binding.root
    }

    private fun bindActions() {
        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }

        binding.addQCourse.setOnClickListener{
            requireActivity().let {
                FragmentChooseCourse().apply {
                    show(it.supportFragmentManager, tag)
                }
            }
        }

        binding.submitQBTN.setOnClickListener {
            if (mEditor.html.isNullOrEmpty()){
                thisContext.toast("Enter your question...")
                mEditor.focusEditor()
            }else if (course==null || course!!.course_code.isNullOrEmpty()){
                context.let {it!!.toast("Select course")}
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = ModelCourses.Factory(requireActivity().application)
        val modelCourses = requireActivity().run{
            ViewModelProvider(this, viewModelFactory).get(ModelCourses::class.java)
        }
        modelCourses.curCourse.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled().let {c->
                binding.addQCourse.text = c?.courseCode()
                course = c
            }
        })


        val vFactory = ModelQuestionsFrag.Factory(requireActivity().application)
        modelQuestionsFrag = requireActivity().run{
            ViewModelProvider(this, vFactory).get(ModelQuestionsFrag::class.java)
        }
    }

    private fun publishContent(content:String = mEditor.html) {
        val retrofit = RetrofitPOST.retrofitWithJsonResponse.create(AskQuestionService::class.java)
        retrofit.askQuestionRequest(
            request_type = "add_question",
            question_from = myDetails.user_id,
            school_id = myDetails.user_school,
            question_body = content,
            imgMap = null,
            course = "${course?.course_id}",
            is_adding_new_question = is_adding_new_question
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
                            thisContext.toast(if (is_adding_new_question) "Question published" else "Question updated")

                            try {//add to database
                                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                                val type = Types.newParameterizedType(MutableList::class.java, Questions::class.java)
                                val adapter: JsonAdapter<List<Questions>> = moshi.adapter(type)
                                val questions: List<Questions> = adapter.fromJson(serverResponse.otherDetail!!)!!
                                modelQuestionsFrag.addToDb(questions)

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
        fun newInstance(param: Parcelable) =
            FragmentAsk().apply {
                arguments = Bundle().apply {
                    putParcelable(QUESTION, param)
                }
            }
    }
}


interface AskQuestionService {
    @Multipart
    @POST("add_question.php")
    fun askQuestionRequest(
//        @Header("Authorization") authorization: String ,
        @Part("request_type") request_type: String,
        @Part("question_from") question_from: Int,
        @Part("school_id") school_id: String,
        @Part("question_body") question_body: String,
        @PartMap imgMap: Map<String, @JvmSuppressWildcards RequestBody>? = null,
        @Part("course") course: String,
        @Part("is_adding_new_question") is_adding_new_question:Boolean, //to know if you are adding or editing(false)
        @Part("image_is_removed") image_is_removed:Boolean = false
    ): Call<ServerResponse>
}