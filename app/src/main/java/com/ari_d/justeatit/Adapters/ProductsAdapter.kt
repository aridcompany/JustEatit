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
import kotlinx.android.synthetic.main.product_layout.view.*
import javax.inject.Inject

class ProductsAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {
    inner class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img_product: ImageView = itemView.img_product
        val btn_addToFavorites: ImageView = itemView.btn_add_to_favorites
        val btn_addToShoppingBag: ImageView = itemView.btn_add_to_bag
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.product_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = products[position]
        holder.apply {
            glide.load(product.images[0]).into(img_product)
            txt_productName.text = product.name
            txt_productPrice.text = "₦" + product.price
            btn_addToFavorites.setImageResource(
                if (product.isAddedToFavorites) {
                    R.drawable.ic_baseline_favorite_24
                } else R.drawable.ic_baseline_favorite_border_24
            )
            btn_addToShoppingBag.setImageResource(
                if (product.isAddedToShoppingBag) {
                    R.drawable.ic_added_to_bag_
                } else R.drawable.ic_bag
            )

            btn_addToFavorites.setOnClickListener {
                onAddToFavoritesClickListener?.let { click ->
                    if (!product.isAddingToFavorites) click(product, holder.layoutPosition)
                }
            }
            btn_addToShoppingBag.setOnClickListener {
                onAddToShoppingBagListener?.let { click ->
                    if (!product.isAddingToShoppingBag) click(product, holder.layoutPosition)
                }
            }
            itemView.setOnClickListener {
                onNavigateToProductDetailsListener?.let { click ->
                    click(product, holder.layoutPosition)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    private var onAddToFavoritesClickListener: ((Product, Int) -> Unit)? = null

    private var onAddToShoppingBagListener: ((Product, Int) -> Unit)? = null

    private var onNavigateToProductDetailsListener: ((Product, Int) -> Unit)? = null

    fun setOnAddToFavoritesClickListener(listener: (Product, Int) -> Unit) {
        onAddToFavoritesClickListener = listener
    }

    fun setOnAddToShoppingBagClickListener(listener: (Product, Int) -> Unit) {
        onAddToShoppingBagListener = listener
    }

    fun setOnNavigateToProductsDetailsClickListener(listener: (Product, Int) -> Unit) {
        onNavigateToProductDetailsListener = listener
    }
}