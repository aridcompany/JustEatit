package com.ari_d.justeatit.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.Wallet
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.credit_card_item.view.*
import javax.inject.Inject

class PaymentViewPagerAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<PaymentViewPagerAdapter.ProductsViewHolder>() {

    inner class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardName: TextView = itemView.txt_card_name
        val cardNumber: TextView = itemView.txt_card_number
        val cardExpiryDate: TextView = itemView.txt_card_exp_date
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Wallet>() {
        override fun areContentsTheSame(oldItem: Wallet, newItem: Wallet): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Wallet, newItem: Wallet): Boolean {
            return oldItem.cardNumber == newItem.cardNumber
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var wallets: List<Wallet>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.credit_card_item,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val wallet = wallets[position]
        holder.apply {
            if (wallet.cardName.length > 15)
                cardName.text = wallet.cardName.substring(0, 16) + "."
            else
                cardName.text = wallet.cardName
            cardNumber.text =
                wallet.cardNumber.substring(0, 4) + " " + "****" + " " + "****" + " " + "****"
            cardExpiryDate.text =
                wallet.expiryDate.substring(0, 2) + "/" + wallet.expiryDate.substring(2, 4)

            itemView.setOnClickListener {
                onGetWalletDetailsClickListener?.let { click ->
                    click(wallet, holder.layoutPosition, cardName)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return wallets.size
    }

    private var onGetWalletDetailsClickListener: ((Wallet, Int, View) -> Unit)? = null

    fun setOnGetWalletDetailsClickListener(listener: (Wallet, Int, View) -> Unit) {
        onGetWalletDetailsClickListener = listener
    }
}