package com.uniedu.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uniedu.databinding.ItemEbookBinding
import com.uniedu.databinding.ItemVideoBinding
import com.uniedu.model.EBooks
import com.uniedu.model.Videos


class AdapterVideo(private val clickListener: VideoClickListener) : RecyclerView.Adapter<AdapterVideo.ViewHolder>() {

    var list: List<Videos> = emptyList()
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
        val binding = ItemVideoBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Videos) {
            binding.video = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

    }

}

//CLICK LISTENER
class VideoClickListener(val clickListener: (Videos) -> Unit) {
    fun onClick(video: Videos) = clickListener(video)
}

