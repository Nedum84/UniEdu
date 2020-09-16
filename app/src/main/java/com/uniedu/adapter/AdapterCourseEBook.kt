package com.uniedu.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.uniedu.R
import com.uniedu.databinding.ItemCoursesEbookBinding
import com.uniedu.model.Courses


class AdapterCourseEBook(private val clickListener: CoursesEBookClickListener) : RecyclerView.Adapter<AdapterCourseEBook.ViewHolder>() {

    var mCtx:Context? = null
    var courses: List<Courses> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = courses.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curItem = courses[position]

        holder.bind(clickListener, curItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
//        val binding = ItemCoursesEbookBinding.inflate(layoutInflater, parent, false)
        val binding: ItemCoursesEbookBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_courses_ebook, parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemCoursesEbookBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: CoursesEBookClickListener, course: Courses) {

            binding.course = course
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

    }

}
//CLICK LISTENER
class CoursesEBookClickListener(val clickListener: (Courses) -> Unit) {
    fun onClick(course: Courses) = clickListener(course)
}

