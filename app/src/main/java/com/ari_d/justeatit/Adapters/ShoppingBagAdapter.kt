package com.ari_d.justeatit.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.Product
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.shopping_bag_layout.view.*
import javax.inject.Inject

class ShoppingBagAdapter @Inject constructor(
    private val glide: RequestManager
): PagingDataAdapter<Product, ShoppingBagAdapter.ShoppingBagViewHolder>(Companion) {
    inner class ShoppingBagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img_product: ImageView = itemView.img_cart_item
        val img_product_: ImageView = itemView.img_cart_item_
        val txt_productName: TextView = itemView.txt_cart_item_name
        val txt_productPrice: TextView = itemView.txt_cart_item_price
        val txt_productQuantity: TextView = itemView.txt_cart_item_quantity
    }

    companion object : DiffUtil.ItemCallback<Product>() {
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.product_id == newItem.product_id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingBagViewHolder {
        return ShoppingBagViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.shopping_bag_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ShoppingBagViewHolder, position: Int) {
        val product = getItem(position) ?: return
        holder.apply {
            glide.load(product.images[0]).into(img_product)
            txt_productName.text = product.name
            txt_productPrice.text = "₦" + product.price
            txt_productQuantity.text = "x" + product.quantity

            if (!product.isAvailable) {
                img_product_.isVisible = true
                txt_productPrice.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.accent))
                txt_productPrice.text = "Out of stock!"
                txt_productQuantity.text = ""
            }

            itemView.setOnClickListener {
                onNavigateToProductDetailsListener?.let { click ->
                    click(product, holder.layoutPosition)
                }
            }
        }
    }
    private var onNavigateToProductDetailsListener: ((Product, Int) -> Unit)? = null

    fun setOnNavigateToProductsDetailsClickListener(listener: (Product, Int) -> Unit) {
        onNavigateToProductDetailsListener = listener
    }
}