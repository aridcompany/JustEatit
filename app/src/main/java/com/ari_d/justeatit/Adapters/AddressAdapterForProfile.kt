package com.ari_d.justeatit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.Address
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.address_item.view.*
import kotlinx.android.synthetic.main.fragment_address_book.*
import kotlinx.android.synthetic.main.shimmer_layout_for_addresses.view.street_address
import javax.inject.Inject

class AddressAdapterForProfile @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<AddressAdapterForProfile.SearchViewHolder>() {
    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val address_street: TextView = itemView.street_address
        val delete_address: ImageView = itemView.delete_address
        val txt_default: TextView = itemView.txt_default
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Address>() {
        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressUID == newItem.addressUID
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var addressses: List<Address>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressAdapterForProfile.SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.address_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AddressAdapterForProfile.SearchViewHolder, position: Int) {
        val address = addressses[position]
        holder.apply {
            address_street.text = address.street_address
            if (address.isDefault)
                txt_default.isVisible = true

            delete_address.setOnClickListener {
                onDeleteAddressClickListener?.let { click ->
                    click(address, holder.layoutPosition, itemView.delete_address)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return addressses.size
    }

    private var onDeleteAddressClickListener: ((Address, Int, View) -> Unit)? = null

    fun setOnDeleteAddressClickListener(listener: (Address, Int, View) -> Unit) {
        onDeleteAddressClickListener = listener
    }
}