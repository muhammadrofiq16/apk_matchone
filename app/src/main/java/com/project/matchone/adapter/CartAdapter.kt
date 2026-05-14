package com.project.matchone.ui.checkout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.matchone.R
import com.project.matchone.data.model.CartItem
import java.text.NumberFormat
import java.util.*

class CartAdapter(
    private var items: List<CartItem>,
    private val listener: OnCartListener
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    // Interface untuk menangani klik di Activity
    interface OnCartListener {
        fun onUpdateQuantity(id: Int, newQty: Int)
        fun onDeleteItem(id: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProduct: ImageView = view.findViewById(R.id.ivProduct)
        val tvName: TextView = view.findViewById(R.id.tvProductName)
        val tvPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val tvQty: TextView = view.findViewById(R.id.tvQuantity)

        // Komponen tombol aksi
        val btnPlus: ImageButton = view.findViewById(R.id.btnPlus)
        val btnMinus: ImageButton = view.findViewById(R.id.btnMinus)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // 1. Set Nama Produk (diambil dari helper get() di CartItem)
        holder.tvName.text = item.name

        // 2. Set Quantity
        holder.tvQty.text = item.quantity.toString()

        // 3. Set Harga (Menggunakan .subtotal sesuai JSON Laravel kamu)
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        // Kita gunakan .subtotal karena variabel .price sudah tidak ada di model
        holder.tvPrice.text = formatRupiah.format(item.subtotal).replace("Rp", "Rp ")

        // 4. Load Gambar menggunakan Glide
        Glide.with(holder.itemView.context)
            .load(item.image)
            .placeholder(R.drawable.ic_launcher_background) // Ganti jika ada placeholder sendiri
            .error(R.drawable.ic_launcher_background)
            .into(holder.ivProduct)

        // --- Logic Klik Tombol ---

        // Tombol Tambah (+)
        holder.btnPlus.setOnClickListener {
            listener.onUpdateQuantity(item.id, item.quantity + 1)
        }

        // Tombol Kurang (-)
        holder.btnMinus.setOnClickListener {
            if (item.quantity > 1) {
                listener.onUpdateQuantity(item.id, item.quantity - 1)
            } else {
                // Jika qty 1 dikurangi, otomatis panggil fungsi delete
                listener.onDeleteItem(item.id)
            }
        }

        // Tombol Hapus (Sampah)
        holder.btnDelete.setOnClickListener {
            listener.onDeleteItem(item.id)
        }
    }

    override fun getItemCount(): Int = items.size

    /**
     * Fungsi krusial: Memperbarui list data saat API selesai memanggil
     * Pastikan dipanggil dari CartActivity: adapter.updateData(response.body()?.data ?: listOf())
     */
    fun updateData(newItems: List<CartItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}