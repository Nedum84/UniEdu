package com.uniedu.adapter


import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uniedu.databinding.ItemAnswerBinding
import com.uniedu.databinding.ItemEbookBinding
import com.uniedu.model.Answers
import com.uniedu.model.EBooks
import com.uniedu.model.Schools
import com.uniedu.ui.fragment.bottomsheet.FragmentAnswerQuestion
import com.uniedu.ui.fragment.bottomsheet.FragmentAsk
import com.uniedu.utils.ClassSharedPreferences


class AdapterAnswer(private val clickListener: AnswerClickListener) : RecyclerView.Adapter<AdapterAnswer.ViewHolder>() {

    var list: List<Answers> = emptyList()
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
        val binding = ItemAnswerBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemAnswerBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Answers) {
            binding.editor.setEditorFontSize(14)
            binding.editor.setInputEnabled(false)
            binding.editor.html = item.answer_body

            binding.editQuestion.visibility =if (ClassSharedPreferences(binding.root.context).getCurUserDetail().user_id == item.answer_from.toInt()) View.VISIBLE else View.GONE
            binding.answer = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

    }

}
//CLICK LISTENER
interface AnswerClickListener {
    fun onClick(answer: Answers)
    fun onEditQuestionClick(answer: Answers)
    fun onClickLikeBTN(answer: Answers)
}


class AdapterAnswer2( val onClickListener: OnClickListener ) : ListAdapter<Answers, AdapterAnswer2.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAnswerBinding.inflate(LayoutInflater.from(parent.context)))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val marsProperty = getItem(position)
        holder.itemView.setOnClickListener {onClickListener.onClick(marsProperty)
        }
        holder.bind(marsProperty)
    }


    class ViewHolder(private var binding: ItemAnswerBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: Answers) {
            binding.answer = answer
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<Answers>() {
        override fun areItemsTheSame(oldItem: Answers, newItem: Answers): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Answers, newItem: Answers): Boolean {
            return oldItem.answer_id == newItem.answer_id
        }
    }



    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [MarsProperty]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [MarsProperty]
     */
    class OnClickListener(val clickListener: (answer:Answers) -> Unit) {
        fun onClick(answer:Answers) = clickListener(answer)
    }
}

