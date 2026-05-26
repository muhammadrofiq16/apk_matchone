package com.project.matchone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.matchone.R
import com.project.matchone.data.model.ProductModel
import java.text.NumberFormat
import java.util.*

class MenuAdapter(
    private val listProduct: List<ProductModel>,
    private val onAddToCartClick: (ProductModel) -> Unit // Ganti nama variabel agar lebih jelas
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProduct: ImageView = itemView.findViewById(R.id.ivProductImage)
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvLabelNew: TextView = itemView.findViewById(R.id.tvLabelNew)

        // 1. INISIALISASI TOMBOL BARU
        val btnAddToCart: Button = itemView.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val product = listProduct[position]

        holder.tvName.text = product.name

        // --- FORMAT HARGA RUPIAH ---
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        val priceDouble = product.price.toDoubleOrNull() ?: 0.0
        holder.tvPrice.text = formatRupiah.format(priceDouble)

        // --- HANDLE STATUS KETERSEDIAAN ---
        if (product.isAvailable == false) {
            holder.tvLabelNew.text = "Sold Out"
            holder.tvLabelNew.visibility = View.VISIBLE

            // Nonaktifkan tombol jika barang habis
            holder.btnAddToCart.isEnabled = false
            holder.btnAddToCart.text = "Habis"
            holder.btnAddToCart.setBackgroundColor(holder.itemView.context.resources.getColor(android.R.color.darker_gray))
            holder.itemView.alpha = 0.6f
        } else {
            holder.tvLabelNew.visibility = View.GONE
            holder.btnAddToCart.isEnabled = true
            holder.btnAddToCart.text = "+ Keranjang"
            holder.itemView.alpha = 1.0f
        }

        // --- KONEKSI CLOUDINARY VIA GLIDE ---
        Glide.with(holder.itemView.context)
            .load(product.image)
            .placeholder(android.R.color.darker_gray)
            .error(android.R.drawable.stat_notify_error)
            .centerCrop()
            .into(holder.ivProduct)

        // --- 2. AKSI KLIK PADA TOMBOL KERANJANG ---
        holder.btnAddToCart.setOnClickListener {
            // Panggil fungsi callback yang akan mengirim data ke MainActivity
            onAddToCartClick(product)
        }

        // --- (Opsional) AKSI KLIK PADA KARTU PRODUK ---
        holder.itemView.setOnClickListener {
            // Nantinya ini bisa kamu gunakan untuk membuka halaman Detail Produk
            // val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            // holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listProduct.size
}