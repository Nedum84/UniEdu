package com.uniedu.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uniedu.databinding.ItemEbookBinding
import com.uniedu.databinding.ItemForSellBinding
import com.uniedu.model.ItemsForSale


class AdapterItemForSale(private val clickListener: ItemForSaleClickListener) : RecyclerView.Adapter<AdapterItemForSale.ViewHolder>() {

    var list: List<ItemsForSale> = emptyList()
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
        val binding = ItemForSellBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemForSellBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemsForSale) {
            binding.item4sale = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

    }

}
//CLICK LISTENER
interface ItemForSaleClickListener {
    fun onClick(item: ItemsForSale)
    fun onMenuClick(item: ItemsForSale, view:View)
}
