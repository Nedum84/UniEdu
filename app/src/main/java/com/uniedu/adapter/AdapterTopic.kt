package com.uniedu.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniedu.databinding.ItemTopicsVidoBinding
import com.uniedu.model.TopicVideos
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassAlertDialog


class AdapterTopic(val viewLifecycleOwner:LifecycleOwner, private val clickListener: TopicClickListener) : RecyclerView.Adapter<AdapterTopic.ViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()

    var list: List<TopicVideos> = emptyList()
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

        fun bind(item: TopicVideos, index:Int) {
            binding.topic = item
            binding.clickListener = clickListener
            binding.idx = index+1


            val childLayoutManager = LinearLayoutManager(binding.videosRecycler.context, LinearLayoutManager.HORIZONTAL, false)
            childLayoutManager.initialPrefetchItemCount = 4
            val ADAPTER = AdapterVideo(VideoClickListener {
                ClassAlertDialog(binding.videosRecycler.context).toast("${it.video_title}")
            })

            binding.videosRecycler.apply {
                layoutManager = childLayoutManager
                adapter = ADAPTER
                setRecycledViewPool(viewPool)
            }

            val db = DatabaseRoom.getDatabaseInstance(binding.root.context)
//            db.videosDao().getByTopic(item.topic_id).observe((binding.root.context as LifecycleOwner), Observer {
            db.videosDao().getByTopic(item.topic_id).observe(viewLifecycleOwner, Observer {
                ADAPTER.list = it
            })
            binding.executePendingBindings()
        }

    }

}
//CLICK LISTENER
class TopicClickListener(val clickListener: (TopicVideos) -> Unit) {
    fun onClick(topic: TopicVideos) = clickListener(topic)
//    fun onClickMenu(topic: TopicVideos) = clickListener(topic)
}



