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
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}