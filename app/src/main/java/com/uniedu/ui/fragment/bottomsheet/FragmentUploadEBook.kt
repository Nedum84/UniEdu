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
import com.uniedu.databinding.FragmentUploadEBookBinding
import com.uniedu.extension.toast
import com.uniedu.model.Courses
import com.uniedu.model.Questions
import com.uniedu.network.AskQuestionService
import com.uniedu.network.RetrofitPOST
import com.uniedu.network.ServerResponse
import com.uniedu.room.DatabaseRoom
import com.uniedu.ui.fragment.BaseFragmentBottomSheetUploadFile
import com.uniedu.ui.fragment.BaseFragmentBottomSheetUploadFile2
import com.uniedu.utils.ClassProgressDialog
import com.uniedu.utils.ClassUtilities
import com.uniedu.viewmodel.ModelCourses
import com.uniedu.viewmodel.ModelQuestionsFrag
import kotlinx.android.synthetic.main.fragment_ask.*
import kotlinx.android.synthetic.main.fragment_upload_e_book.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

private const val QUESTION = "question"

class FragmentUploadEBook : BaseFragmentBottomSheetUploadFile2() {
    private var question: Questions? = null
    lateinit var binding:FragmentUploadEBookBinding
    lateinit var databaseRoom: DatabaseRoom

    lateinit var modelQuestionsFrag: ModelQuestionsFrag

    private var course: Courses? = null
    private var is_adding_new_question = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            question = it.getParcelable(QUESTION)
            is_adding_new_question = false

            if (!question!!.question_image_path.isNullOrEmpty()){
                imagePreviewWrapper.visibility = View.VISIBLE
                pickImage.visibility = View.GONE
                Glide.with(this).load(imageFilePath).into(imagePreview)
            }
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_e_book, container, false)

        bindEvents()

        return binding.root
    }

    private fun bindEvents() {
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
        binding.pickImage.setOnClickListener {
            imagePickerDialog()
        }
        binding.submitQBTN.setOnClickListener {
            submitQuestion()
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

    private fun submitQuestion(){
        val question_body = binding.questionBody.text.toString().trim()

        if (question_body.isEmpty() && imageFilePath.isNullOrEmpty()){
            context.let {it!!.toast("No question entered")}
        }else if (course==null || course!!.course_code.isNullOrEmpty()){
            context.let {it!!.toast("Select course")}
        }else{
            ClassUtilities().hideKeyboard(binding.root, thisContext)
            val pDialog = ClassProgressDialog(thisContext)
            pDialog.createDialog()

            // Map is used to multipart the file using okhttp3.RequestBody
            val map = HashMap<String, RequestBody>()
            val imgFile = File("$imageFilePath")
            // Parsing any Media type file
            val requestBody     = RequestBody.create(MediaType.parse("*/*"), imgFile)
            map["file\"; filename=\"" + imgFile.name + "\""] = requestBody


//            val file2 = File("gifImgPath")
//            val requestBodyImg2  = RequestBody.create(MediaType.parse("*/*"), file2)
//            map["file2\"; filename=\"" + file2.name + "\""] = requestBodyImg2

            val imgMap = if (!imageFilePath.isNullOrEmpty())  map else HashMap<String, RequestBody>()

            val logComplainService = RetrofitPOST.retrofitWithJsonResponse.create(AskQuestionService::class.java)
            logComplainService.askQuestionRequest(
                "add_question",
                myDetails.user_id,
                myDetails.user_school,
                "$question_body",
                imgMap,
                "${course?.course_id}",
                is_adding_new_question,
                is_image_removed
            ).enqueue(object: Callback<ServerResponse> {
                override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                    pDialog.dismissDialog()
                    requireContext().toast("No internet connect!")
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                    pDialog.dismissDialog()
                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            val serverResponse = response.body()

                            if (!(serverResponse!!.success as Boolean)){
                                context!!.toast(serverResponse.respMessage!!)
                            }else{
                                thisContext.toast(if (is_adding_new_question) "Question published" else "Question updated")

                                try {//add to database
                                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                                    val type = Types.newParameterizedType(MutableList::class.java,Questions::class.java)
                                    val adapter: JsonAdapter<List<Questions>> = moshi.adapter(type)
                                    val questions: List<Questions> = adapter.fromJson(serverResponse.otherDetail!!)!!
                                    modelQuestionsFrag.addToDb(questions)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                questionSubmitted()
                            }
                        }
                    } else {
                        context!!.toast("An error occurred, Try again")
                    }
                }

            })
        }
    }

    private fun questionSubmitted() {
        imagePreview?.setImageDrawable(null)
        imagePreviewWrapper?.visibility = View.GONE

        pickImage.visibility = View.VISIBLE
        imageFilePath = null
        prefs.setImgUploadPath("")

        binding.questionBody.setText("")
        binding.addQCourse.text = "Select Course"
        dismiss()
        dialog?.dismiss()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
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