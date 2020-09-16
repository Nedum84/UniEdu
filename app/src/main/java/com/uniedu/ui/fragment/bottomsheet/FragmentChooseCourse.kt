package com.uniedu.ui.fragment.bottomsheet

import android.app.AlertDialog
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.uniedu.R
import com.uniedu.adapter.AdapterCourses
import com.uniedu.adapter.CourseClickListener
import com.uniedu.databinding.FragmentChooseCourseBinding
import com.uniedu.extension.toast
import com.uniedu.model.Courses
import com.uniedu.network.RetrofitPOST
import com.uniedu.network.ServerResponse
import com.uniedu.ui.fragment.BaseFragment
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.utils.ClassProgressDialog
import com.uniedu.viewmodel.ModelCourses
import kotlinx.android.synthetic.main.dialog_add_course.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

private const val COURSE = "course_arg"
class FragmentChooseCourse : BaseFragment() {
    lateinit var binding:FragmentChooseCourseBinding
    lateinit var modelCourses: ModelCourses
    lateinit var ADAPTER:AdapterCourses
    var is_adding_course:Boolean = true
    var course:Courses? = null

    override fun onStart() {
        super.onStart()
        arguments?.let {
            course = it.getParcelable(COURSE)
            is_adding_course = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_course, container, false)

        val viewModelFactory = ModelCourses.Factory(requireActivity().application)
        modelCourses = ViewModelProvider(this, viewModelFactory).get(ModelCourses::class.java)
        binding.apply {
            lifecycleOwner = this@FragmentChooseCourse
        }

        ADAPTER = AdapterCourses(clickListener = CourseClickListener {
            modelCourses.setCurCourse(it)
            //close the modal
            thisContext.toast("${it.course_title} Clicked/...")

            //When editing is clicked
            is_adding_course = false
            addCourseDialog(it)
        })
        modelCourses.courses.observe(viewLifecycleOwner, Observer {
            ADAPTER.list = it
        })

        binding.newCourseBTN.setOnClickListener {
            addCourseDialog()
        }

        return binding.root
    }

    private var addCourseAlertDialog:AlertDialog? = null
    private fun addCourseDialog(editCourse:Courses?=null) {
        val inflater = LayoutInflater.from(thisContext).inflate(R.layout.dialog_add_course, null)
        val builder = AlertDialog.Builder(thisContext)

        builder.setView(inflater)
        addCourseAlertDialog = builder.create()
        addCourseAlertDialog?.show()

        if (!is_adding_course){
            inflater.addCourseCode.setText(editCourse?.course_code)
            inflater.addCourseCode.setText(editCourse?.course_title)
        }

        inflater.addCourseBtn.setOnClickListener {

            val courseCode = inflater.addCourseCode.text.toString().trim()
            val courseTitle = inflater.addCourseTitle.text.toString().trim()

            if (courseCode.isEmpty()) {
                ClassAlertDialog(thisContext).toast("Enter course code")
            } else if (courseTitle.isEmpty()) {
                ClassAlertDialog(thisContext).toast("Enter course title")
            } else {

                //creating volley string request
                val dialog = ClassProgressDialog(thisContext)
                dialog.createDialog()
                //Sending the request

                RetrofitPOST.retrofitWithJsonResponse.create(AddCourse::class.java).addCourse(
                    courseCode,
                    courseTitle,
                    is_adding_course,
                    editCourse?.course_id
                ).enqueue(object : Callback<ServerResponse>{
                    override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                        thisContext.toast("NETWORK ERROR")
                    }

                    override fun onResponse(call: Call<ServerResponse>,response: Response<ServerResponse>) {
                        if (!response.isSuccessful){
                            thisContext.toast("NETWORK ERROR")
                        }else{
                            val resp = response.body()
                            if (!resp!!.success!!){
                                thisContext.toast(resp.respMessage!!)
                            }else{
                                val recentCourses = resp.otherDetail
                                //add to database
                            }
                        }
                    }

                })
            }
        }
    }




    companion object {

        @JvmStatic
        fun newInstance(param: Parcelable) =
            FragmentChooseCourse().apply {
                arguments = Bundle().apply {
                    putParcelable(COURSE, param)
                }
            }
    }
}
interface AddCourse{

    @Multipart
    @POST("add_course.php")
    fun addCourse(
        @Part("course_code") course_code:String,
        @Part("course_title") course_title: String,
        @Part("is_adding_course") is_adding:Boolean,
        @Part("course_id") course_id:Int? = null
    ): Call<ServerResponse>
}