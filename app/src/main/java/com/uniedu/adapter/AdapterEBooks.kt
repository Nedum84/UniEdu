package com.uniedu.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uniedu.databinding.ItemEbookBinding
import com.uniedu.model.EBooks


class AdapterEBooks(private val clickListener: EBookClickListener) : RecyclerView.Adapter<AdapterEBooks.ViewHolder>() {

    var list: List<EBooks> = emptyList()
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
        val binding = ItemEbookBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemEbookBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EBooks) {
            binding.ebook = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

    }

}
//CLICK LISTENER
class EBookClickListener(val clickListener: (EBooks) -> Unit) {
    fun onClick(book: EBooks) = clickListener(book)
}

