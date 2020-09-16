package com.uniedu.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uniedu.databinding.ItemCourseBinding
import com.uniedu.databinding.ItemEbookBinding
import com.uniedu.databinding.ItemVideoBinding
import com.uniedu.model.Courses
import com.uniedu.model.EBooks
import com.uniedu.model.Videos


class AdapterCourses(private val clickListener: CourseClickListener) : RecyclerView.Adapter<AdapterCourses.ViewHolder>() {

    var list: List<Courses> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curItem = list[position]

        holder.bind(curItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCourseBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Courses) {
            binding.course = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

    }

}

//CLICK LISTENER
class CourseClickListener(val clickListener: (Courses) -> Unit) {
    fun onClick(course: Courses) = clickListener(course)
    fun onEditClick(course: Courses) = clickListener(course)
}

