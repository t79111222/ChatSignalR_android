package tw.com.intersense.signalrchat.ui.chat

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tw.com.intersense.signalrchat.data.database.repository.message.Message
import tw.com.intersense.signalrchat.databinding.ItemMessageBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MessageAdapter internal constructor(
    private val fragment: Fragment,
    private var myPhoneId: String,
    private var phpneDensity: Float,
) :
    ListAdapter<Message, MessageAdapter.ViewHolder>(MessageDiffCallback()) {
    val dp50 = (phpneDensity * 50).toInt()
    val dp16 = (phpneDensity * 16).toInt()

    override fun submitList(list: List<Message>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        if(item.SpeakerPhoneId == myPhoneId){
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
        if(item.MessageType == "Sticker"){
            holder.binding.tvMessage.visibility = View.GONE
            holder.binding.sticker.visibility = View.VISIBLE
            Glide.with(fragment).load(item.MessageText).into(holder.binding.sticker)
        }
        else{
            holder.binding.tvMessage.visibility = View.VISIBLE
            holder.binding.sticker.visibility = View.GONE
        }
    }

    class ViewHolder private constructor(val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        fun bind(item: Message) {
            binding.tvMessage.text = item.MessageText
            item.CreateTime?.let {
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
        return (oldItem.ProductId == newItem.ProductId && oldItem.AskerPhoneId == newItem.AskerPhoneId)
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}
