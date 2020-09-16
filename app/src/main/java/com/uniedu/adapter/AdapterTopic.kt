package com.uniedu.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.databinding.ItemTopicsVidoBinding
import com.uniedu.model.Topics


class AdapterTopic(private val clickListener: TopicClickListener) : RecyclerView.Adapter<AdapterTopic.ViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()

    var list: List<Topics> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curItem = list[position]

        holder.bind(curItem, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTopicsVidoBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemTopicsVidoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Topics, index:Int) {
            binding.topic = item
            binding.clickListener = clickListener
            binding.idx = index


            val childLayoutManager = LinearLayoutManager(binding.videosRecycler.context, LinearLayoutManager.HORIZONTAL, false)
            childLayoutManager.initialPrefetchItemCount = 4
            val ADAPTER = AdapterVideo(VideoClickListener {
                ClassAlertDialog(binding.videosRecycler.context).toast("${it.video_name}")
            })
            binding.videosRecycler.apply {
                layoutManager = childLayoutManager
                adapter = ADAPTER
                setRecycledViewPool(viewPool)
            }


            binding.executePendingBindings()
        }

    }

}
//CLICK LISTENER
class TopicClickListener(val clickListener: (Topics) -> Unit) {
    fun onClick(topic: Topics) = clickListener(topic)
    fun onClickMenu(topic: Topics) = clickListener(topic)
}

