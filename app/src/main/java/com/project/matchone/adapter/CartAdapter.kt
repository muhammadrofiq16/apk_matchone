package com.project.matchone.ui.checkout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.matchone.R
import com.project.matchone.data.model.CartItem

class CartAdapter(
    private var items: List<CartItem>,
    private val listener: OnCartListener
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    interface OnCartListener {
        fun onUpdateQuantity(id: Int, newQty: Int)
        fun onDeleteItem(id: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProduct: ImageView = view.findViewById(R.id.ivProduct)
        val tvName: TextView = view.findViewById(R.id.tvProductName)
        val tvPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val tvQty: TextView = view.findViewById(R.id.tvQuantity)
        val btnPlus: Button = view.findViewById(R.id.btnPlus)
        val btnMinus: Button = view.findViewById(R.id.btnMinus)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvPrice.text = "Rp ${item.price}"
        holder.tvQty.text = item.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(item.image)
            .placeholder(R.color.cardview_dark_background)
            .into(holder.ivProduct)

        holder.btnPlus.setOnClickListener { listener.onUpdateQuantity(item.id, item.quantity + 1) }
        holder.btnMinus.setOnClickListener {
            if (item.quantity > 1) listener.onUpdateQuantity(item.id, item.quantity - 1)
        }
        holder.btnDelete.setOnClickListener { listener.onDeleteItem(item.id) }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}