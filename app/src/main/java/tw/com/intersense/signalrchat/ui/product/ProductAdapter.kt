package tw.com.intersense.signalrchat.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tw.com.intersense.signalrchat.R
import tw.com.intersense.signalrchat.data.network.Product
import tw.com.intersense.signalrchat.databinding.ItemProductBinding
import java.time.format.DateTimeFormatter

class ProductAdapter internal constructor (
    private val fragment: Fragment,
    private var clickItem:(poduct: Product)->Unit) :
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
        var urlProduct = ""
        item.imageLink?.let {
            val listProductImage =it.split(",").toTypedArray()
            if(listProductImage.isNotEmpty()) urlProduct = listProductImage[0]
        }
        Glide.with(fragment).load(urlProduct).placeholder(R.drawable.shipping).into(holder.binding.ivProduct)
        Glide.with(fragment).load(item.ownerImageLink).placeholder(R.drawable.user).into(holder.binding.ivUser)
    }

    class ViewHolder private constructor(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        fun bind(item: Product) {
            binding.tvProductName.text = item.name
            binding.tvUesrName.text = item.ownerName
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
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
