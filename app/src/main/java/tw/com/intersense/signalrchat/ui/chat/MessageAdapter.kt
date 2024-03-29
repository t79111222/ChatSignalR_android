package tw.com.intersense.signalrchat.ui.chat

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.intersense.signalrchat.data.database.repository.message.Message
import tw.com.intersense.signalrchat.databinding.ItemMessageBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MessageAdapter internal constructor(
    private var userId: String,
    private var phpneDensity: Float,
) :
    ListAdapter<Message, MessageAdapter.ViewHolder>(MessageDiffCallback()) {
    val dp50 = (phpneDensity * 50).toInt()
    val dp16 = (phpneDensity * 16).toInt()

    override fun submitList(list: List<Message>?) {
        val let = list?.let { ArrayList(it) }
        super.submitList(let)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        if(item.userId == userId){
            holder.binding.llLayout.setPadding(dp50,dp16,dp16,dp16)
            holder.binding.llLayout.gravity = Gravity.RIGHT
            holder.binding.tvMessage.setBackgroundColor(Color.parseColor("#FF2EC71F"))
            holder.binding.tvMessage.setTextColor(Color.parseColor("#FFFFFFFF"))
        }
        else{
            holder.binding.llLayout.setPadding(dp16,dp16,dp50,dp16)
            holder.binding.llLayout.gravity = Gravity.LEFT
            holder.binding.tvMessage.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
            holder.binding.tvMessage.setTextColor(Color.parseColor("#FF000000"))
        }
    }

    class ViewHolder private constructor(val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        fun bind(item: Message) {
            binding.tvMessage.text = item.text
            item.time?.let {
                var instant = Instant.ofEpochSecond(it)
                var localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                binding.tvDate.text = dateTimeFormatter.format(localDateTime)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMessageBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}
