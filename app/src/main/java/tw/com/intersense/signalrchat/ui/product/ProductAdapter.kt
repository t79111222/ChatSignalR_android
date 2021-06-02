package tw.com.intersense.signalrchat.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.intersense.signalrchat.data.network.Product
import tw.com.intersense.signalrchat.databinding.ItemProductBinding
import java.time.format.DateTimeFormatter

class ProductAdapter internal constructor (private var clickItem:(poduct: Product)->Unit) :
    ListAdapter<Product, ProductAdapter.ViewHolder>(ProductDiffCallback()) {


    override fun submitList(list: List<Product>?) {
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
            clickItem(item)
        }
    }

    class ViewHolder private constructor(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        fun bind(item: Product) {
            binding.tvProductName.text = item.name
            binding.tvUesrName.text = item.userName
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemProductBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
