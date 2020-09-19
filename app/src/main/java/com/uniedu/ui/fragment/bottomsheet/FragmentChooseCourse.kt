package com.uniedu.ui.fragment.bottomsheet

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.PEEK_HEIGHT_AUTO
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.uniedu.R
import com.uniedu.adapter.AdapterCourses
import com.uniedu.adapter.CourseClickListener
import com.uniedu.adapter.CourseClickListener2
import com.uniedu.databinding.FragmentChooseCourseBinding
import com.uniedu.extension.toast
import com.uniedu.model.Courses
import com.uniedu.network.RetrofitPOST
import com.uniedu.network.ServerResponse
import com.uniedu.ui.fragment.BaseFragment
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.utils.ClassProgressDialog
import com.uniedu.utils.ClassUtilities
import com.uniedu.viewmodel.ModelCourses
import kotlinx.android.synthetic.main.dialog_add_course.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

class FragmentChooseCourse : BaseFragment() {
    lateinit var binding:FragmentChooseCourseBinding
    lateinit var modelCourses: ModelCourses
    lateinit var ADAPTER:AdapterCourses
    var is_adding_course:Boolean = true
    var course:Courses? = null

    private var addCourseAlertDialog:AlertDialog? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = ModelCourses.Factory(requireActivity().application)
        modelCourses = requireActivity().run{
            ViewModelProvider(this, viewModelFactory).get(ModelCourses::class.java)
        }

        modelCourses.queryString.observe(viewLifecycleOwner, Observer {query->
            query?.getContentIfNotHandled()?.let {
                modelCourses.courses(it).observe(viewLifecycleOwner, Observer {
                    it?.let {
                        ADAPTER.list = it
                        checkEmpty()
                    }
                })
            }
        })
        searchCourses()
    }

    override fun onStart() {
        super.onStart()
        modelCourses.setSearchQuery("")
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_course, container, false)

        val viewModelFactory = ModelCourses.Factory(requireActivity().application)
        modelCourses = ViewModelProvider(this, viewModelFactory).get(ModelCourses::class.java)
        binding.apply {
            lifecycleOwner = this@FragmentChooseCourse
        }

//        ADAPTER = AdapterCourses(clickListener = CourseClickListener {
//            modelCourses.setCurCourse(it)
//            dialog?.dismiss()
//
//
//            //When editing is clicked
////            is_adding_course = false
////            addCourseDialog(it)
//        })
        ADAPTER = AdapterCourses(object : CourseClickListener2{
            override fun onClick(course: Courses) {
                modelCourses.setCurCourse(course)
                dialog?.dismiss()
            }

            override fun onEditClick(course: Courses) {
                is_adding_course = false
                addCourseDialog(course)
            }

        })

        binding.recyclerCourses.apply {
            adapter = ADAPTER
            layoutManager= LinearLayoutManager(activity)
        }

        modelCourses.feedBack.observe(viewLifecycleOwner, Observer {
            checkEmpty(true)
        })
        adapterListener()


        binding.newCourseBTN.setOnClickListener {
            is_adding_course = true
            addCourseDialog()
        }

        //Refresh Courses
        binding.refreshCourse.setOnClickListener {
            modelCourses.refreshCourse()
        }

        return binding.root
    }
    private fun searchCourses(searchView: SearchView = binding.searchCourse){
        searchView.queryHint = "Enter course code..."
        searchView.setIconifiedByDefault(false)


        val searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as TextView
        searchText.setTextColor(Color.BLACK)
        searchText.setHintTextColor(Color.BLACK)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                modelCourses.setSearchQuery(query!!)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                modelCourses.setSearchQuery(query)

                return true
            }
        })
    }

    private fun  adapterListener(){
        ADAPTER.registerAdapterDataObserver(object :RecyclerView.AdapterDataObserver(){

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                checkEmpty()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                checkEmpty()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                checkEmpty()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
                checkEmpty()
            }
        })

    }
    fun checkEmpty(is_network_error:Boolean = false){
        if (is_network_error){
            binding.noCourseFound.visibility = if (ADAPTER.itemCount==0) View.VISIBLE else View.GONE
            binding.noCourseTitle.text = "No internet connection"
        }else{
            binding.noCourseTitle.text = "No Course found"
            binding.noCourseFound.visibility = if (ADAPTER.itemCount==0) View.VISIBLE else View.GONE
        }
    }


    private fun addCourseDialog(editCourse:Courses?=null) {
        val inflater = LayoutInflater.from(thisContext).inflate(R.layout.dialog_add_course, null)
        val builder = AlertDialog.Builder(thisContext)

        builder.setView(inflater)
        addCourseAlertDialog = builder.create()
        addCourseAlertDialog?.show()

        if (!is_adding_course){
            inflater.addCourseCode.setText(editCourse?.course_code)
            inflater.addCourseTitle.setText(editCourse?.course_title)
        }

        inflater.addCourseBtn.setOnClickListener {
            val courseCode = inflater.addCourseCode.text.toString().trim()
            val courseTitle = inflater.addCourseTitle.text.toString().trim()

            if (courseCode.isEmpty()) {
                ClassAlertDialog(thisContext).toast("Enter course code")
            } else if (courseTitle.isEmpty()) {
                ClassAlertDialog(thisContext).toast("Enter course title")
            } else {
                ClassUtilities().hideKeyboard(binding.root, thisContext)
                val pDialog = ClassProgressDialog(thisContext)
                pDialog.createDialog()
                //Sending the request

                RetrofitPOST.retrofitWithJsonResponse.create(AddCourseService::class.java).addCourse(
                    "add_course",
                    courseCode,
                    courseTitle,
                    myDetails.user_school,
                    is_adding_course,
                    editCourse?.course_id
                ).enqueue(object : Callback<ServerResponse>{
                    override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                        pDialog.dismissDialog()
                        thisContext.toast("NETWORK ERROR")
                        t.printStackTrace()
                    }

                    override fun onResponse(call: Call<ServerResponse>,response: Response<ServerResponse>) {
                        pDialog.dismissDialog()
                        if (!response.isSuccessful){
                            thisContext.toast("NETWORK ERROR")
                        }else{
                            val resp = response.body()
                            if (!resp!!.success!!){
                                thisContext.toast(resp.respMessage!!)
                            }else{
                                thisContext.toast(if (is_adding_course) "Course added" else "Course updated")
                                addCourseAlertDialog?.hide()

                                try {//add to database
                                    val moshi: Moshi = Moshi.Builder().build()
//                                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                                    val type = Types.newParameterizedType(MutableList::class.java,Courses::class.java)
                                    val adapter: JsonAdapter<List<Courses>> = moshi.adapter(type)
                                    val courses: List<Courses> = adapter.fromJson(resp.otherDetail!!)!!
                                    modelCourses.addToDb(courses)

//                                    OR------>
//                                    val collectionType: Type = object : TypeToken<List<Courses>>() {}.type
//                                    val courses: List<Courses> = Gson().fromJson(resp.otherDetail!!, collectionType)


//                                    OR------>
//                                    val courses =  Gson().fromJson(resp.otherDetail!!, Array<Courses>::class.java).asList()
                                    modelCourses.addToDb(courses)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }

                })
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.peekHeight = PEEK_HEIGHT_AUTO
//            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels-200
        }
    }

//    OR
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val bottomSheetDialog =
//            super.onCreateDialog(savedInstanceState) as BottomSheetDialog
//        bottomSheetDialog.setOnShowListener { dialog: DialogInterface ->
//            val dialogc = dialog as BottomSheetDialog
//            // When using AndroidX the resource can be found at com.google.android.material.R.id.design_bottom_sheet
//            val bottomSheet: FrameLayout? =
//                dialogc.findViewById(com.google.android.material.R.id.design_bottom_sheet)
//            val bottomSheetBehavior: BottomSheetBehavior<*> =
//                BottomSheetBehavior.from(bottomSheet!!)
//            bottomSheetBehavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
////            bottomSheetBehavior.peekHeight = PEEK_HEIGHT_AUTO
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
//        }
//        return bottomSheetDialog
//    }

    //OR
    //    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = BottomSheetDialog(requireContext(), theme)
//        dialog.setOnShowListener {
//
//            val bottomSheetDialog = it as BottomSheetDialog
//            val parentLayout =  bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
//            parentLayout?.let { it ->
//                val behaviour = BottomSheetBehavior.from(it)
//                setupFullHeight(it)
//                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
//            }
//        }
//        return dialog
//    }
    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }
}
interface AddCourseService{

    @Multipart
    @POST("add_course.php")
    fun addCourse(
        @Part("request_type") request_type:String,
        @Part("course_code") course_code:String,
        @Part("course_title") course_title: String,
        @Part("school_id") school_id: String,
        @Part("is_adding_course") is_adding_course:Boolean,
        @Part("course_id") course_id:Int? = null
    ): Call<ServerResponse>
}