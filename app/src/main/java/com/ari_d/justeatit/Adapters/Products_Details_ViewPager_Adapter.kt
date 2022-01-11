package com.ari_d.justeatit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ari_d.justeatit.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.product_images_item.view.*

class Products_Details_ViewPager_Adapter (
    val images: List<String>
) : RecyclerView.Adapter<Products_Details_ViewPager_Adapter.ProductsViewHolder>() {

    inner class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img_product : ImageView = itemView.img_product_details
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.product_images_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val image = images[position]
        holder.apply {
          Glide.with(itemView.context).load(image).into(img_product)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }
}