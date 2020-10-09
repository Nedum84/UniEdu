package com.uniedu.ui.fragment

import android.app.ProgressDialog.show
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.uniedu.R
import com.uniedu.adapter.*
import com.uniedu.databinding.FragmentVideoBinding
import com.uniedu.extension.toast
import com.uniedu.model.Courses
import com.uniedu.ui.fragment.bottomsheet.FragmentItemForSaleDetail
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.viewmodel.ModelTopicVideos

class FragmentVideo : BaseFragment() {
    lateinit var binding: FragmentVideoBinding

    lateinit var modelTopicVideos: ModelTopicVideos
    lateinit var ADAPTER: AdapterCourseVideo

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video, container, false)


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        ADAPTER = AdapterCourseVideo(VideoCoursesClickListener {course->
            requireActivity().let {
                FragmentTopicVideo.newInstance(course).apply {
                    show(it.supportFragmentManager, tag)
                }
            }
        }).apply { VIEW_FROM_HOME = false }

        binding.recyclerVideoCourses.apply {
            adapter = ADAPTER
            layoutManager= LinearLayoutManager(thisContext)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = false
        }


        val vFactory = ModelTopicVideos.Factory(application)
        modelTopicVideos =  requireActivity().run{
            ViewModelProvider(this, vFactory).get(ModelTopicVideos::class.java)
        }

        modelTopicVideos.topicVideos.observe(viewLifecycleOwner, Observer {
            it?.let{
                val courseList = it.map {it.course_id.toInt()}.distinct()//distinct course id from the topics
//                val videoCourses = (it.distinctBy { it.course_id }).map { it.course_id }//distinct course id from the topics

                db.coursesDao.getByIds(courseList).observe(viewLifecycleOwner, Observer {
                    ADAPTER.courses = it
                })
            }
        })
    }
}