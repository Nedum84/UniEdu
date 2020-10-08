package com.uniedu.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uniedu.databinding.ItemCourseBinding
import com.uniedu.databinding.ItemEbookBinding
import com.uniedu.databinding.ItemForSaleCategoryBinding
import com.uniedu.databinding.ItemVideoBinding
import com.uniedu.model.Courses
import com.uniedu.model.EBooks
import com.uniedu.model.ItemCategory
import com.uniedu.model.Videos


class AdapterItemForSaleCategory(private val clickListener: ItemForSaleCatClickListener) : RecyclerView.Adapter<AdapterItemForSaleCategory.ViewHolder>() {

    var list: List<ItemCategory> = emptyList()
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
        val binding = ItemForSaleCategoryBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemForSaleCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemCategory) {
            binding.itemCategory = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

    }

}

//CLICK LISTENER
class ItemForSaleCatClickListener(val clickListener: (ItemCategory) -> Unit) {
    fun onClick(itemCategory: ItemCategory) = clickListener(itemCategory)
}

