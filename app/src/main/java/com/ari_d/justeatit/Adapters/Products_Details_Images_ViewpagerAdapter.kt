package com.ari_d.justeatit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.Product
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.product_images_item.view.*
import javax.inject.Inject

class Products_Details_Images_ViewpagerAdapter @Inject constructor(
    private val glide : RequestManager
) : RecyclerView.Adapter<Products_Details_Images_ViewpagerAdapter.ProductsDetailsImagesViewHolder>() {
    inner class ProductsDetailsImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val product_images : ImageView = itemView.img_product_details
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
    ): ProductsDetailsImagesViewHolder {
       return ProductsDetailsImagesViewHolder(
           LayoutInflater.from(parent.context)
               .inflate(
                   R.layout.product_images_item,
                   parent,
                   false
               )
       )
    }

    override fun onBindViewHolder(holder: ProductsDetailsImagesViewHolder, position: Int) {
        val product = products[position]
        holder.itemView.apply {
            glide.load(product.images).into(img_product_details)
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }
}