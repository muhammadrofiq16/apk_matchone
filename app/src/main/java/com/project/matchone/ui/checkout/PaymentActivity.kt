package com.project.matchone.ui.checkout

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.matchone.R
import com.project.matchone.data.model.CartItem
import com.project.matchone.data.model.Order
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

class PaymentActivity : AppCompatActivity() {

    private lateinit var tvTotalPayment: TextView
    private lateinit var rgPaymentMethod: RadioGroup
    private lateinit var btnConfirmPayment: Button

    private var totalAmount: Double = 0.0
    // Tambahkan variabel untuk menampung data produk yang dibeli
    private var cartItems: List<CartItem> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // 1. Inisialisasi View
        tvTotalPayment = findViewById(R.id.tvTotalPayment)
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod)
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment)

        // 2. Ambil total tagihan dari Intent (dikirim dari Cart)
        totalAmount = intent.getDoubleExtra("EXTRA_TOTAL_AMOUNT", 0.0)

        // TODO: Ambil data cartItems dari Intent atau Database Lokal (Room/SQLite)
        // Jika dari Intent (Pastikan CartItem menggunakan implementasi Parcelable atau Serializable)
        // cartItems = intent.getParcelableArrayListExtra<CartItem>("EXTRA_CART_ITEMS") ?: listOf()

        // 3. Tampilkan format Rupiah
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        tvTotalPayment.text = formatRupiah.format(totalAmount).replace("Rp", "Rp ")

        // 4. Set aksi tombol konfirmasi
        btnConfirmPayment.setOnClickListener {
            processOrder()
        }
    }

    private fun processOrder() {
        // Cek apakah metode pembayaran sudah dipilih
        val selectedPaymentId = rgPaymentMethod.checkedRadioButtonId
        if (selectedPaymentId == -1) {
            Toast.makeText(this, "Silakan pilih metode pembayaran!", Toast.LENGTH_SHORT).show()
            return
        }

        // Ambil nama metode pembayaran yang dipilih
        val selectedRadioButton: RadioButton = findViewById(selectedPaymentId)
        val paymentMethod = selectedRadioButton.text.toString()

        // Generate ID Pesanan (misal: ORD-A1B2C3D4)
        val newOrderId = "ORD-${UUID.randomUUID().toString().substring(0, 8).uppercase()}"

        // BENTUK OBJEK ORDER (Sekarang sudah lengkap dengan items)
        val order = Order(
            orderId = newOrderId,
            userId = "USER-123", // TODO: Nanti ganti dengan ID User yang sedang login
            items = cartItems,   // -> INI PENTING: Memasukkan daftar produk ke dalam pesanan
            totalAmount = totalAmount,
            paymentMethod = paymentMethod,
            status = "MENUNGGU_KONFIRMASI", // Status awal untuk diproses Admin
            orderDate = System.currentTimeMillis()
        )

        saveOrderToDatabase(order)
    }

    private fun saveOrderToDatabase(order: Order) {
        // Simulasi sukses:
        Toast.makeText(this, "Pesanan ${order.orderId} sedang diproses!\nMetode: ${order.paymentMethod}", Toast.LENGTH_LONG).show()

        // TODO: Taruh logika Firebase Firestore / Database di sini nanti

        // Pindah ke halaman utama atau halaman "Sukses"
        finish()
    }
}