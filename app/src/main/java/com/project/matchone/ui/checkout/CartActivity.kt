package com.project.matchone.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.project.matchone.R
import com.project.matchone.data.model.CartItem
import com.project.matchone.data.model.CheckoutResponse
import com.project.matchone.data.network.ApiClient
import com.project.matchone.ui.main.CatalogActivity
import com.project.matchone.ui.main.HomeActivity
import com.project.matchone.ui.profile.ProfileActivity
import com.project.matchone.utils.CartRepository
import com.project.matchone.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity(), CartAdapter.OnCartListener {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotalPrice: TextView
    private lateinit var tvTotalPriceBottom: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var btnBack: android.view.View
    private lateinit var btnClear: android.view.View
    private lateinit var btnCheckout: MaterialButton
    private lateinit var btnTambahPesanan: TextView
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

        initViews()
        setupRecyclerView()
        setupClickListeners()
        setupBottomNav()
        loadCart()
    }

    override fun onResume() {
        super.onResume()
        loadCart()
    }

    private fun initViews() {
        rvCart = findViewById(R.id.rvCart)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        tvTotalPriceBottom = findViewById(R.id.tvTotalPriceBottom)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        btnBack = findViewById(R.id.btnBack)
        btnClear = findViewById(R.id.btnClearCart)
        btnCheckout = findViewById(R.id.btnCheckout)
        btnTambahPesanan = findViewById(R.id.btnTambahPesanan)
    }

    private fun setupRecyclerView() {
        rvCart.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(emptyList(), this)
        rvCart.adapter = cartAdapter
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }
        btnTambahPesanan.setOnClickListener { finish() }
        btnClear.setOnClickListener { clearAllCart() }
        btnCheckout.setOnClickListener { processCheckout() }
    }

    private fun setupBottomNav() {
        // Highlight tab Keranjang sebagai aktif
        val activeColor  = android.graphics.Color.parseColor("#2D5A27")
        val activeColorStateList = ContextCompat.getColorStateList(this, android.R.color.holo_green_dark)

        try {
            findViewById<ImageView>(R.id.iconCart)
                ?.setColorFilter(activeColor)
            findViewById<TextView>(R.id.textCart)
                ?.setTextColor(activeColor)
        } catch (e: Exception) { /* ignore */ }

        // Navigasi ke tab lain
        findViewById<LinearLayout>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.navCatalog)?.setOnClickListener {
            startActivity(Intent(this, CatalogActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
        // navCart tidak perlu listener karena sudah di halaman ini
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
                btnCheckout.isEnabled = false
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun updateTotal(items: List<CartItem>) {
        val total = items.sumOf { it.subtotal.toDouble() }
        val localeID = Locale("in", "ID")
        val fmt = NumberFormat.getCurrencyInstance(localeID)
        val formatted = fmt.format(total).replace("Rp", "Rp ")

        tvTotalPrice.text = formatted
        tvTotalPriceBottom.text = formatted
        tvSubtotal.text = formatted
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
            onSuccess = { loadCart() },
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
                Toast.makeText(this, "Keranjang dikosongkan", Toast.LENGTH_SHORT).show()
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

        val authToken = sessionManager.fetchAuthToken()

        if (authToken.isNullOrEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan, silakan login ulang", Toast.LENGTH_SHORT).show()
            return
        }

        btnCheckout.isEnabled = false
        btnCheckout.text = "Memproses..."

        val token = "Bearer $authToken"

        ApiClient.instance.checkoutCart(token).enqueue(object : Callback<CheckoutResponse> {

            override fun onResponse(call: Call<CheckoutResponse>, response: Response<CheckoutResponse>) {
                btnCheckout.isEnabled = true
                btnCheckout.text = "Checkout →"

                if (response.isSuccessful) {
                    val order = response.body()?.order

                    if (order == null) {
                        Toast.makeText(this@CartActivity, "Data order tidak ditemukan", Toast.LENGTH_LONG).show()
                        return
                    }

                    val totalAmount = cartItems.sumOf { it.subtotal.toDouble() }
                    val itemsSummary = cartItems.joinToString("\n") { item ->
                        "${item.product?.name ?: "Produk"} x${item.quantity}"
                    }

                    Toast.makeText(this@CartActivity, "Pesanan berhasil dibuat!", Toast.LENGTH_LONG).show()

                    startActivity(Intent(this@CartActivity, PaymentActivity::class.java).apply {
                        putExtra("EXTRA_ORDER_ID", order.id)
                        putExtra("EXTRA_TOTAL_AMOUNT", totalAmount)
                        putExtra("EXTRA_PAYMENT_METHOD", "")
                        putExtra("EXTRA_ITEMS_SUMMARY", itemsSummary)
                    })
                    finish()

                } else {
                    Toast.makeText(this@CartActivity, "Gagal checkout: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
                btnCheckout.isEnabled = true
                btnCheckout.text = "Checkout →"
                Toast.makeText(this@CartActivity, "Koneksi bermasalah: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}