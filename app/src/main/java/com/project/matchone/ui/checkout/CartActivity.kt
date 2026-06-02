package com.project.matchone.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
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
        loadCart()
    }

    override fun onResume() {
        super.onResume()
        loadCart()
    }

    private fun initViews() {
        rvCart              = findViewById(R.id.rvCart)
        tvTotalPrice        = findViewById(R.id.tvTotalPrice)
        tvTotalPriceBottom  = findViewById(R.id.tvTotalPriceBottom)
        tvSubtotal          = findViewById(R.id.tvSubtotal)
        btnBack             = findViewById(R.id.btnBack)
        btnClear            = findViewById(R.id.btnClearCart)
        btnCheckout         = findViewById(R.id.btnCheckout)
        btnTambahPesanan    = findViewById(R.id.btnTambahPesanan)
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

        tvTotalPrice.text       = formatted
        tvTotalPriceBottom.text = formatted
        tvSubtotal.text         = formatted
    }

    override fun onUpdateQuantity(id: Int, newQty: Int) {
        if (isUpdatingCart) return
        if (newQty <= 0) { onDeleteItem(id); return }
        isUpdatingCart = true
        cartRepository.updateCart(
            cartId = id, newQuantity = newQty,
            onSuccess = { isUpdatingCart = false; loadCart() },
            onError = { msg -> isUpdatingCart = false; Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
        )
    }

    override fun onDeleteItem(id: Int) {
        cartRepository.deleteItem(
            cartId = id,
            onSuccess = { loadCart() },
            onError = { msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
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
            onError = { msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
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
                btnCheckout.text = "Checkout →"

                if (response.isSuccessful && response.body() != null) {
                    val order = response.body()!!.order

                    // Buat ringkasan item untuk struk
                    val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                    val itemsSummary = order.orderItems.joinToString("\n") { item ->
                        val subtotal = formatRupiah.format(
                            (item.priceAtPurchase.toDoubleOrNull() ?: 0.0) * item.qty
                        ).replace("Rp", "Rp ")
                        "• Produk #${item.productId}  x${item.qty}  →  $subtotal"
                    }

                    // Buka StrukActivity dengan data order
                    val intent = Intent(this@CartActivity, StrukActivity::class.java).apply {
                        putExtra("INVOICE_NUMBER", order.invoiceNumber)
                        putExtra("TOTAL_PRICE", order.totalPrice)
                        putExtra("STATUS", order.status)
                        putExtra("PAYMENT_METHOD", order.paymentMethod)
                        putExtra("CREATED_AT", order.createdAt)
                        putExtra("ITEMS_SUMMARY", itemsSummary)
                    }
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(
                        this@CartActivity,
                        "Gagal checkout: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
                btnCheckout.isEnabled = true
                btnCheckout.text = "Checkout →"
                Toast.makeText(
                    this@CartActivity,
                    "Koneksi bermasalah: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}