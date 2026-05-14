package com.project.matchone.ui.checkout

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.matchone.R
import com.project.matchone.data.model.CartItem
import com.project.matchone.data.model.CheckoutResponse
import com.project.matchone.data.network.ApiClient
import com.project.matchone.utils.CartRepository
import com.project.matchone.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity(), CartAdapter.OnCartListener {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnClear: Button
    private lateinit var btnCheckout: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var cartAdapter: CartAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var cartRepository: CartRepository

    private var cartItems: List<CartItem> = emptyList()
    private var isUpdatingCart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        sessionManager = SessionManager(this)

        val token = sessionManager.fetchAuthToken() ?: ""
        cartRepository = CartRepository(token)

        rvCart = findViewById(R.id.rvCart)
        tvTotal = findViewById(R.id.tvTotalPrice)
        btnClear = findViewById(R.id.btnClearCart)
        btnCheckout = findViewById(R.id.btnCheckout)

        rvCart.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(emptyList(), this)
        rvCart.adapter = cartAdapter

        loadCart()

        btnClear.setOnClickListener { clearAllCart() }
        btnCheckout.setOnClickListener { processCheckout() }
    }

    override fun onResume() {
        super.onResume()
        loadCart()
    }

    private fun loadCart() {
        btnCheckout.isEnabled = false

        cartRepository.getCart(
            onSuccess = { items ->
                cartItems = items
                cartAdapter.updateData(items)
                btnCheckout.isEnabled = items.isNotEmpty()
                updateTotal(items)
            },
            onError = { msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun updateTotal(items: List<CartItem>) {
        val total = items.sumOf { it.subtotal.toDouble() }

        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        tvTotal.text = formatRupiah.format(total).replace("Rp", "Rp ")
    }

    override fun onUpdateQuantity(id: Int, newQty: Int) {
        if (isUpdatingCart) return

        if (newQty <= 0) {
            onDeleteItem(id)
            return
        }

        isUpdatingCart = true

        cartRepository.updateCart(
            cartId = id,
            newQuantity = newQty,
            onSuccess = {
                isUpdatingCart = false
                loadCart()
            },
            onError = { msg ->
                isUpdatingCart = false
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDeleteItem(id: Int) {
        cartRepository.deleteItem(
            cartId = id,
            onSuccess = {
                loadCart()
            },
            onError = { msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun clearAllCart() {
        cartRepository.clearCart(
            onSuccess = {
                cartItems = emptyList()
                cartAdapter.updateData(emptyList())
                updateTotal(emptyList())
                btnCheckout.isEnabled = false
                Toast.makeText(this, "Keranjang dibersihkan", Toast.LENGTH_SHORT).show()
            },
            onError = { msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun processCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Keranjang masih kosong!", Toast.LENGTH_SHORT).show()
            return
        }

        btnCheckout.isEnabled = false
        btnCheckout.text = "Memproses..."

        val token = "Bearer ${sessionManager.fetchAuthToken()}"

        ApiClient.instance.checkoutCart(token).enqueue(object : Callback<CheckoutResponse> {
            override fun onResponse(
                call: Call<CheckoutResponse>,
                response: Response<CheckoutResponse>
            ) {
                btnCheckout.isEnabled = true
                btnCheckout.text = "Lanjut ke Pembayaran"

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@CartActivity,
                        "Pesanan berhasil dibuat!",
                        Toast.LENGTH_LONG
                    ).show()
                    loadCart()
                } else {
                    Toast.makeText(
                        this@CartActivity,
                        "Gagal membuat pesanan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
                btnCheckout.isEnabled = true
                btnCheckout.text = "Lanjut ke Pembayaran"
                Toast.makeText(
                    this@CartActivity,
                    "Koneksi bermasalah: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}x