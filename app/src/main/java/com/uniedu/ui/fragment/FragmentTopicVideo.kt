package com.uniedu.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.uniedu.R
import com.uniedu.adapter.AdapterTopic
import com.uniedu.adapter.TopicClickListener
import com.uniedu.databinding.FragmentTopicVideoBinding
import com.uniedu.extension.makeFullScreen
import com.uniedu.model.Courses
import com.uniedu.ui.fragment.bottomsheet.FragmentEBookDetail
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.viewmodel.ModelTopicVideos


private const val COURSE = "course"

class FragmentTopicVideo : BaseFragmentBottomSheet() {
    lateinit var binding:FragmentTopicVideoBinding

    lateinit var modelTopicVideo: ModelTopicVideos
    lateinit var ADAPTER: AdapterTopic

    lateinit var course: Courses





    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            course = it.getParcelable(COURSE)!!
            binding.toolbar.title = course.courseCodeAndTitle()
        }

        ADAPTER = AdapterTopic(viewLifecycleOwner, TopicClickListener {
            ClassAlertDialog(thisContext).toast("${it.topic_name}...")
        })

        binding.recyclerTopicVideo.apply {
            adapter = ADAPTER
            layoutManager= LinearLayoutManager(thisContext)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = false
        }


        val vFactory = ModelTopicVideos.Factory(application)
        modelTopicVideo =  requireActivity().run{
            ViewModelProvider(this, vFactory).get(ModelTopicVideos::class.java)
        }

        modelTopicVideo.topicVideos.observe(viewLifecycleOwner, Observer {
            it?.let{
                ADAPTER.list = it.filter { it.course_id.toInt() == course.course_id }
            }
        })
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_topic_video, container, false)




        return binding.root
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = context?.makeFullScreen(this)
        return dialog!!
    }



    companion object {

        @JvmStatic
        fun newInstance(param: Parcelable) =
            FragmentTopicVideo().apply {
                arguments = Bundle().apply {
                    putParcelable(COURSE, param)
                }
            }
    }
}

