package com.uniedu.bindingadapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.uniedu.R
import com.uniedu.room.DatabaseRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ClassTextBinding {

    @JvmStatic // add this line !!
    @BindingAdapter("bindCourseCode")
    fun bindCourseCode(view: TextView, course_id: String){
        val db = DatabaseRoom.getDatabaseInstance(view.context)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val course = db.coursesDao.getById(course_id.toInt())
                view.text = course!!.course_code
            } catch (e: Exception) {
                e.printStackTrace()
                view.text = course_id
            }
        }


    }


}