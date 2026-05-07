package com.project.matchone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.matchone.R
import com.project.matchone.data.model.ProductModel
import java.text.NumberFormat
import java.util.*

class MenuAdapter(private val listProduct: List<ProductModel>) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProduct: ImageView = itemView.findViewById(R.id.ivProductImage)
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvLabelNew: TextView = itemView.findViewById(R.id.tvLabelNew)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val product = listProduct[position]

        holder.tvName.text = product.name

        // Format Harga ke Rupiah
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        
        // Handle String price "40000.00"
        val priceDouble = product.price.toDoubleOrNull() ?: 0.0
        holder.tvPrice.text = formatRupiah.format(priceDouble)

        // Sesuai JSON Laravel: is_available
        if (product.isAvailable == false) {
            holder.tvLabelNew.text = "Sold Out"
            holder.tvLabelNew.visibility = View.VISIBLE
        } else {
            holder.tvLabelNew.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            // Nanti arahkan ke DetailActivity
        }
    }

    override fun getItemCount(): Int = listProduct.size
}
