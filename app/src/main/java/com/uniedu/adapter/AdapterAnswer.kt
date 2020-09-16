package com.uniedu.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uniedu.databinding.ItemAnswerBinding
import com.uniedu.databinding.ItemEbookBinding
import com.uniedu.model.Answers
import com.uniedu.model.EBooks


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
            binding.answer = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

    }

}
//CLICK LISTENER
class AnswerClickListener(val clickListener: (Answers) -> Unit) {
    fun onClick(answer: Answers) = clickListener(answer)
    fun onClickLikeBTN(answer: Answers) = clickListener(answer)
}

