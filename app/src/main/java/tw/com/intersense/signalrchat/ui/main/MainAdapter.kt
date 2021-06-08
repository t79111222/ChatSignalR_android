package tw.com.intersense.signalrchat.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tw.com.intersense.signalrchat.data.database.repository.chat.Chat
import tw.com.intersense.signalrchat.databinding.ItemChatBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.view.View
import tw.com.intersense.signalrchat.R


class MainAdapter internal constructor (
    private val fragment: Fragment,
    private val myPhoneId: String,
    private var clickItem: (productId: Int, askerPhoneId:String) -> Unit): ListAdapter<Chat, MainAdapter.ViewHolder>(ChatDiffCallback()) {


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
            clickItem(item.ProductId, item.AskerPhoneId)
        }
        var urlProduct = ""
        item.ProductImagesString?.let {
            val listProductImage =it.split(",").toTypedArray()
            if(listProductImage.isNotEmpty()) urlProduct = listProductImage[0]
        }
        Glide.with(fragment).load(urlProduct).placeholder(R.drawable.shipping).into(holder.binding.ivProduct)

        var urlOtherUser: String? = ""
        if(item.AskerPhoneId != myPhoneId){
            urlOtherUser = item.AskerImageLink
        }
        else{
            urlOtherUser = item.OwnerImageLink
        }
        Glide.with(fragment).load(urlOtherUser).placeholder(R.drawable.user).into(holder.binding.ivOtherUser)

    }

    class ViewHolder private constructor(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        fun bind(item: Chat) {
            binding.tvName.text = item.ProductName
            binding.tvDescription.text = ""
            binding.tvDate.text = ""
            binding.tvCount.text = item.NotReadCount.toString()
            binding.tvCount.visibility = if(item.NotReadCount > 0) View.VISIBLE else View.GONE
            item.LastMessageType?.let {
                binding.tvDescription.text = if(item.LastMessageType == "Sticker")"[貼圖]" else (item.LastMessageText ?: "")
            }
            item.LastMessageTime?.let {
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
        return (oldItem.ProductId == newItem.ProductId && oldItem.AskerPhoneId == newItem.AskerPhoneId)
    }

    override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem == newItem
    }
}
