package com.uniedu.adapter


import android.annotation.SuppressLint
import android.app.Application
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uniedu.databinding.ItemQuestionBinding
import com.uniedu.model.Courses
import com.uniedu.model.Questions
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassDateAndTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AdapterQuestion(val app:Application, val clickListener: QuestionClickListener) : ListAdapter<Questions, AdapterQuestion.ViewHolder>(QuestionsDiffCallback()) {


    private val adapterScope = CoroutineScope(Dispatchers.Default)
    var databaseRoom = DatabaseRoom.getDatabaseInstance(app)

    fun addNewItems(list: List<Questions>?) {
        adapterScope.launch {
//            val items = list?.map {it}
            withContext(Dispatchers.Main) {
                submitList(list)
            }
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curItem = getItem(position)

        holder.bind(clickListener, curItem,databaseRoom)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: QuestionClickListener, question: Questions, db: DatabaseRoom) {

            try {
//                CoroutineScope(Dispatchers.Main).launch {
//                    try {
//                        val course = db.coursesDao.getById(question.course_id.toInt())
//                        binding.courseCode.text = course!!.course_code
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        binding.courseCode.text = question.course_id
//                    }
//                }

                binding.question = question
                binding.clickListener = clickListener
                binding.db = db
                binding.executePendingBindings()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemQuestionBinding.inflate(layoutInflater, parent, false)
//                val binding2: ItemQuestionBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_rape_type_form, parent, false)
                return ViewHolder(binding)
            }
        }
    }


}
//CLICK LISTENER
class QuestionClickListener(val clickListener: (Questions) -> Unit) {
    fun onClick(item: Questions) = clickListener(item)
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minumum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class QuestionsDiffCallback : DiffUtil.ItemCallback<Questions>() {
    override fun areItemsTheSame(oldItem: Questions, newItem: Questions): Boolean {
        return oldItem.question_id == newItem.question_id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Questions, newItem: Questions): Boolean {
        return oldItem == newItem
    }
}
