package com.uniedu.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.uniedu.R
import com.uniedu.databinding.ItemCoursesVideoBinding
import com.uniedu.databinding.ItemCoursesVideoHomeBinding
import com.uniedu.model.Courses


class AdapterCourseVideo(private val clickListenerVideo: VideoCoursesClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var VIEW_FROM_HOME = true
    var mCtx:Context? = null
    var courses: List<Courses> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = courses.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curItem = courses[position]

        if(VIEW_FROM_HOME){
            (holder as ViewHolderForHome).bind(clickListenerVideo, curItem)
        }else{
            (holder as ViewHolder).bind(clickListenerVideo, curItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val bindingHome = ItemCoursesVideoHomeBinding.inflate(layoutInflater, parent, false)
        val binding: ItemCoursesVideoBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_courses_video, parent, false)
//        mCtx = parent.context
        return if (VIEW_FROM_HOME){
            ViewHolderForHome(bindingHome)
        }else{
            ViewHolder(binding)
        }
    }

    inner class ViewHolder(val binding: ItemCoursesVideoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListenerVideo: VideoCoursesClickListener, course: Courses) {
            binding.course = course
            binding.clickListener = clickListenerVideo
            binding.executePendingBindings()
        }

    }
    inner class ViewHolderForHome(val binding: ItemCoursesVideoHomeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListenerVideo: VideoCoursesClickListener, course: Courses) {
            binding.course = course
            binding.clickListener = clickListenerVideo
            binding.executePendingBindings()
        }
    }

    companion object{

    }
}
//CLICK LISTENER
class VideoCoursesClickListener(val clickListener: (Courses) -> Unit) {
    fun onClick(course: Courses) = clickListener(course)
}

