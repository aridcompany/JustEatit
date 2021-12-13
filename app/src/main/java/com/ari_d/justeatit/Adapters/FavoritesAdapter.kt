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
import com.ari_d.justeatit.data.entities.Favorite
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.favorites_layout.view.*
import javax.inject.Inject

class FavoritesAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {
    inner class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img_product: ImageView = itemView.img_product
        val txt_productName: TextView = itemView.txt_product_name
        val txt_productPrice: TextView = itemView.txt_product_price
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Favorite>() {
        override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
            return oldItem.product_id == newItem.product_id
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var favorites: List<Favorite>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritesAdapter.FavoritesViewHolder {
        return FavoritesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.favorites_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FavoritesAdapter.FavoritesViewHolder, position: Int) {
        val favorite = favorites[position]
        holder.apply {
            glide.load(favorite.images[0]).into(img_product)
            txt_productName.text = favorite.name
            txt_productPrice.text = "₦" + favorite.price

            itemView.setOnClickListener {
                onNavigateToProductDetailsClickListener?.let { click ->
                    click(favorite, holder.layoutPosition)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return favorites.size
    }

    private var onNavigateToProductDetailsClickListener: ((Favorite, Int) -> Unit)? = null

    fun setOnNavigateToProductDetailsClickListener(listener: (Favorite, Int) -> Unit) {
        onNavigateToProductDetailsClickListener = listener
    }
}