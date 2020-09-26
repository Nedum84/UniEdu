package com.uniedu.ui.fragment.bottomsheet

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
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
import com.uniedu.databinding.FragmentUploadEBookBinding
import com.uniedu.extension.addOnClickListener
import com.uniedu.extension.makeFullScreen
import com.uniedu.extension.setAllOnClickListener
import com.uniedu.extension.toast
import com.uniedu.model.Courses
import com.uniedu.model.EBooks
import com.uniedu.model.Questions
import com.uniedu.network.RetrofitPOST
import com.uniedu.network.ServerResponse
import com.uniedu.network.UploadEBookService
import com.uniedu.room.DatabaseRoom
import com.uniedu.ui.fragment.BaseFragmentBottomSheetUploadFile2
import com.uniedu.utils.ClassProgressDialog
import com.uniedu.utils.ClassUtilities
import com.uniedu.viewmodel.ModelCourses
import com.uniedu.viewmodel.ModelQuestionsFrag
import kotlinx.android.synthetic.main.bottomsheet_ebook_upload_type.view.*
import kotlinx.android.synthetic.main.fragment_upload_e_book.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

private const val EBOOK = "question"

class FragmentUploadEBook : BaseFragmentBottomSheetUploadFile2() {
    private var ebook: EBooks? = null
    lateinit var binding:FragmentUploadEBookBinding

    lateinit var modelQuestionsFrag: ModelQuestionsFrag

    private var course: Courses? = null
    private var is_adding_new_question = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ebook = it.getParcelable(EBOOK)
            ebook?.let {
                imagePreviewWrapper.visibility = View.VISIBLE
                pickImage.visibility = View.GONE
                Glide.with(this).load(imageFilePath).into(imagePreview)


                is_adding_new_question = false
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
        binding.submitQBTN.setOnClickListener {
            submitQuestion()
        }


        binding.pickImage.setOnClickListener {
            bottomSheetDialog()
        }
    }



    private fun disableBottomSheetDraggableBehavior() {
        this.isCancelable = false
        this.dialog?.setCanceledOnTouchOutside(true)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        disableBottomSheetDraggableBehavior()

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


            val imgMap = if (!imageFilePath.isNullOrEmpty())  map else HashMap<String, RequestBody>()

            val uploadEbook = RetrofitPOST.retrofitWithJsonResponse.create(UploadEBookService::class.java)
            uploadEbook.upload(
                "add_question",
                myDetails.user_id,
                myDetails.user_school,
                "$question_body",
                imgMap,
                "${course?.course_id}",
                is_adding_new_question
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


    //Make full screen
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = context?.makeFullScreen(this)
        return dialog!!
    }

    companion object {

        @JvmStatic
        fun newInstance(ebook: Parcelable? = null) =
            FragmentUploadEBook().apply {
                arguments = Bundle().apply {
                    putParcelable(EBOOK, ebook)
                }
            }
    }
}