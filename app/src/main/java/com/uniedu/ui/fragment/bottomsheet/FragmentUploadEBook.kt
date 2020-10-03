package com.uniedu.ui.fragment.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.uniedu.R
import com.uniedu.databinding.FragmentUploadEBookBinding
import com.uniedu.extension.makeFullScreen
import com.uniedu.extension.toast
import com.uniedu.model.Courses
import com.uniedu.model.EBooks
import com.uniedu.network.RetrofitPOST
import com.uniedu.network.ServerResponse
import com.uniedu.network.UploadEBookService
import com.uniedu.ui.fragment.BaseFragmentBottomSheetUploadFile2
import com.uniedu.utils.ClassUtilities
import com.uniedu.viewmodel.ModelCourses
import com.uniedu.viewmodel.ModelEbook
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

    lateinit var modelEbookFrag: ModelEbook

    private var course: Courses? = null
    var is_adding_new = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ebook = it.getParcelable(EBOOK)
            ebook?.let {
                imagePreviewWrapper.visibility = View.VISIBLE
                pickImage.visibility = View.GONE
                Glide.with(this).load(postFilePath).into(imagePreview)


                is_adding_new = false
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
        binding.publishEbook.setOnClickListener {
            submitEbook()
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


        val vFactory = ModelEbook.Factory(requireActivity().application)
        modelEbookFrag = requireActivity().run{
            ViewModelProvider(this, vFactory).get(ModelEbook::class.java)
        }
    }

    private fun submitEbook(){
        val book_title = binding.bookTitle.text.toString().trim()
        val book_desc = binding.bookDesc.text.toString().trim()

        if (book_title.isEmpty()){
            context.let {it!!.toast("Enter the title")}
        }else if (postFilePath.isNullOrEmpty()){
            context.let {it!!.toast("Select File")}
        }else if (course==null || course!!.course_code.isNullOrEmpty()){
            context.let {it!!.toast("Select course")}
        }else{
            ClassUtilities().hideKeyboard(binding.root, thisContext)
            pDialog.createDialog()

            // Map is used to multipart the file using okhttp3.RequestBody
            val fileMap = HashMap<String, RequestBody>()
            val file = File("$postFilePath")
            val requestBody     = RequestBody.create(MediaType.parse("*/*"), file)
            fileMap["file\"; filename=\"" + file.name + "\""] = requestBody

            //For PDF Preview Map
            if (fileType == "pdf" && !pdfImgCover.isNullOrEmpty()){
                val pdfFileCover = File(pdfImgCover)
                val pdfRequestBody     = RequestBody.create(MediaType.parse("*/*"), pdfFileCover)
                fileMap["pdf_file_cover\"; filename=\"" + pdfFileCover.name + "\""] = pdfRequestBody
            }

            val uploadEbook = RetrofitPOST.retrofitWithJsonResponse.create(UploadEBookService::class.java)
            uploadEbook.upload(
                request_type = "add_ebook",
                book_id = ebook?.book_id,
                book_uploaded_from = myDetails.user_id,
                book_title = book_title,
                book_desc = book_desc,
                fileMap = fileMap,
                book_type = fileType,
                course_id = "${course?.course_id}",
                school_id = myDetails.user_school,
                is_adding_new = is_adding_new,
                book_no_of_pages = mPdfRenderer?.pageCount
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
                                thisContext.toast(if (is_adding_new) "Book published" else "Book updated")

                                try {//add to database
                                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                                    val type = Types.newParameterizedType(MutableList::class.java, EBooks::class.java)
                                    val adapter: JsonAdapter<List<EBooks>> = moshi.adapter(type)
                                    val questions: List<EBooks> = adapter.fromJson(serverResponse.otherDetail!!)!!
                                    modelEbookFrag.addToDb(questions)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                bookPublished()
                            }
                        }
                    } else {
                        context!!.toast("An error occurred, Try again")
                    }
                }

            })
        }
    }


    private fun bookPublished() {
        imagePreview?.setImageDrawable(null)
        imagePreviewWrapper?.visibility = View.GONE

        pickImage.visibility = View.VISIBLE
        postFilePath = null
        prefs.setImgUploadPath("")

        binding.bookTitle.setText("")
        binding.bookDesc.setText("")
        binding.addQCourse.text = "Select Course"
        is_adding_new = true
        ebook= null
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