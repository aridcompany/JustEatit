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
import com.ari_d.justeatit.data.entities.Address
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.address_item.view.*
import kotlinx.android.synthetic.main.shimmer_layout_for_addresses.view.*
import kotlinx.android.synthetic.main.shimmer_layout_for_addresses.view.address_city
import kotlinx.android.synthetic.main.shimmer_layout_for_addresses.view.apt__name
import kotlinx.android.synthetic.main.shimmer_layout_for_addresses.view.phone_number
import kotlinx.android.synthetic.main.shimmer_layout_for_addresses.view.street_address
import javax.inject.Inject

class AddressAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<AddressAdapter.SearchViewHolder>() {
    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val address_street: TextView = itemView.street_address
        val apt_suite: TextView = itemView.apt__name
        val address_city: TextView = itemView.address_city
        val phone_number: TextView = itemView.phone_number
        val delete_address: ImageView = itemView.delete_address
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
    ): AddressAdapter.SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.address_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AddressAdapter.SearchViewHolder, position: Int) {
        val address = addressses[position]
        holder.apply {
            address_street.text = address.street_address
            apt_suite.text = address.apt_suite
            address_city.text = address.city
            phone_number.text = address.phone_number

            delete_address.setOnClickListener {
                onDeleteAddressClickListener?.let { click ->
                    click(address, holder.layoutPosition)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return addressses.size
    }

    private var onDeleteAddressClickListener: ((Address, Int) -> Unit)? = null

    fun setOnDeleteAddressClickListener(listener: (Address, Int) -> Unit) {
        onDeleteAddressClickListener = listener
    }
}