package com.ari_d.justeatit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.Account_Items
import kotlinx.android.synthetic.main.account_items_menu.view.*
import javax.inject.Inject

class MainProfileFragmentAdapter @Inject constructor(

) : RecyclerView.Adapter<MainProfileFragmentAdapter.MainProfileFragmentViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Account_Items>() {
        override fun areContentsTheSame(oldItem: Account_Items, newItem: Account_Items): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Account_Items, newItem: Account_Items): Boolean {
            return oldItem.title == newItem.title
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var items: List<Account_Items>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class MainProfileFragmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainProfileFragmentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.account_items_menu, parent, false)
        return MainProfileFragmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainProfileFragmentViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.apply {
            txt_item.text = items[position].title
            btn_1st_img.setImageResource(items[position].img)

           holder.itemView.setOnClickListener {
               if (txt_item.text == context.getString(R.string.title_address_book)) {
                   onAddressBookClickListener?.let { click ->
                       click(item)
                   }
               } else  if (txt_item.text == context.getString(R.string.title_my_wallet)) {
                   onWalletClickListener?.let { click ->
                       click(item)
                   }
               } else  if (txt_item.text == context.getString(R.string.title_my_orders)) {
                   onMyOrdersClickListener?.let { click ->
                       click(item)
                   }
               } else  if (txt_item.text == context.getString(R.string.title_track_orders)) {
                   onTrackMyOrdersClickListener?.let { click ->
                       click(item)
                   }
               }
           }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private var onAddressBookClickListener: ((Account_Items) -> Unit)? = null

    private var onWalletClickListener: ((Account_Items) -> Unit)? = null

    private var onMyOrdersClickListener: ((Account_Items) -> Unit)? = null

    private var onTrackMyOrdersClickListener: ((Account_Items) -> Unit)? = null

    fun setOnAddressBookClickListener(listener: (Account_Items) -> Unit) {
        onAddressBookClickListener = listener
    }

    fun setOnWalletClickListener(listener: (Account_Items) -> Unit) {
        onWalletClickListener = listener
    }

    fun setOnMyOrdersClickListener(listener: (Account_Items) -> Unit) {
        onMyOrdersClickListener = listener
    }

    fun setOnTrackMyOrdersClickListener(listener: (Account_Items) -> Unit) {
        onTrackMyOrdersClickListener = listener
    }
}