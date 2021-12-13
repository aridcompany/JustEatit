package com.ari_d.justeatit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.Product
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.search_layout.view.*
import javax.inject.Inject

class searchAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<searchAdapter.SearchViewHolder>() {
    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img_product: ImageView = itemView.img_product
        val txt_productName: TextView = itemView.txt_product_name
        val txt_productPrice: TextView = itemView.txt_product_price
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.product_id == newItem.product_id
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var products: List<Product>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): searchAdapter.SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.search_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: searchAdapter.SearchViewHolder, position: Int) {
        val product = products[position]
        holder.apply {
            glide.load(product.images[0]).into(img_product)
            txt_productName.text = product.name
            txt_productPrice.text = "₦" + product.price

            itemView.setOnClickListener {
                onNavigateToProductDetailsClickListener?.let { click ->
                    click(product, holder.layoutPosition)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    private var onNavigateToProductDetailsClickListener: ((Product, Int) -> Unit)? = null

    fun setOnNavigateToProductDetailsClickListener(listener: (Product, Int) -> Unit) {
        onNavigateToProductDetailsClickListener = listener
    }
}