package com.ari_d.justeatit.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.Orders
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_order_history.view.*
import javax.inject.Inject

class OrdersAdapter_Test @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<OrdersAdapter_Test.OrdersViewHolder>() {
    inner class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img_Orders: ImageView = itemView.order_image
        val txt_OrdersName: TextView = itemView.order_name
        val txt_OrdersPrice: TextView = itemView.order_price
        val txt_OrdersId: TextView = itemView.order_id
        val txt_OrdersDate: TextView = itemView.order_date
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Orders>() {
        override fun areContentsTheSame(oldItem: Orders, newItem: Orders): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Orders, newItem: Orders): Boolean {
            return oldItem.orderID == newItem.orderID
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var products: List<Orders>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_order_history,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val Orders = products[position]
        holder.apply {
            glide.load(Orders.image).into(img_Orders)
            txt_OrdersName.text = Orders.name
            txt_OrdersPrice.text = "₦" + Orders.price
            txt_OrdersId.text = itemView.context.getString(R.string.title_order_id) +  Orders.orderID.substring(0, 12)
            txt_OrdersDate.text = Orders.timeStamp

            itemView.setOnClickListener {
                onNavigateToOrdersDetailsListener?.let { click ->
                    click(Orders, holder.layoutPosition)
                }
            }
        }
    }

    private var onNavigateToOrdersDetailsListener: ((Orders, Int) -> Unit)? = null

    fun setOnNavigateToOrderssDetailsClickListener(listener: (Orders, Int) -> Unit) {
        onNavigateToOrdersDetailsListener = listener
    }

    override fun getItemCount(): Int {
        return products.size
    }
}