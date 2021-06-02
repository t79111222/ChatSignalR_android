package tw.com.intersense.signalrchat.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.intersense.signalrchat.data.database.repository.chat.Chat
import tw.com.intersense.signalrchat.databinding.ItemChatBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainAdapter internal constructor (private var clickItem:(chatId: Int)->Unit) :
    ListAdapter<Chat, MainAdapter.ViewHolder>(ChatDiffCallback()) {


    override fun submitList(list: List<Chat>?) {
        val let = list?.let { ArrayList(it) }
        super.submitList(let)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener{
            clickItem(item.id)
        }
    }

    class ViewHolder private constructor(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        fun bind(item: Chat) {
            binding.tvName.text = if(item.productName.isNullOrEmpty())item.name else item.productName
            binding.tvDescription.text = item.lastMessage
            item.lastTime?.let {
                var instant = Instant.ofEpochSecond(it)
                var localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                binding.tvDate.text = dateTimeFormatter.format(localDateTime)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemChatBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem == newItem
    }
}
