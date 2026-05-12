package com.project.matchone.ui.checkout

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.matchone.R
import com.project.matchone.data.model.CartItem
import com.project.matchone.data.model.CartResponse
import com.project.matchone.data.model.CartSummary
import com.project.matchone.data.network.ApiClient
import com.project.matchone.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartActivity : AppCompatActivity(), CartAdapter.OnCartListener {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnClear: Button
    private lateinit var cartAdapter: CartAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        sessionManager = SessionManager(this)

        rvCart = findViewById(R.id.rvCart)
        tvTotal = findViewById(R.id.tvTotalPrice)
        btnClear = findViewById(R.id.btnClearCart)

        rvCart.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(emptyList(), this)
        rvCart.adapter = cartAdapter

        loadCart()
        loadSummary()

        btnClear.setOnClickListener { clearAllCart() }
    }

    private fun getAuthToken(): String {
        val token = sessionManager.fetchAuthToken() // Menggunakan fetchAuthToken agar konsisten dengan SessionManager
        return if (token != null) "Bearer $token" else ""
    }

    private fun loadCart() {
        ApiClient.instance.getCart(getAuthToken()).enqueue(object : Callback<List<CartItem>> {
            override fun onResponse(call: Call<List<CartItem>>, response: Response<List<CartItem>>) {
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    cartAdapter.updateData(list)
                }
            }
            override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                Toast.makeText(this@CartActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadSummary() {
        ApiClient.instance.getCartSummary(getAuthToken()).enqueue(object : Callback<CartSummary> {
            override fun onResponse(call: Call<CartSummary>, response: Response<CartSummary>) {
                if (response.isSuccessful) {
                    tvTotal.text = "Total: Rp ${response.body()?.total_price ?: 0}"
                }
            }
            override fun onFailure(call: Call<CartSummary>, t: Throwable) {}
        })
    }

    override fun onUpdateQuantity(id: Int, newQty: Int) {
        ApiClient.instance.updateCart(getAuthToken(), id, newQty).enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (response.isSuccessful) {
                    loadCart()
                    loadSummary()
                }
            }
            override fun onFailure(call: Call<CartResponse>, t: Throwable) {}
        })
    }

    override fun onDeleteItem(id: Int) {
        ApiClient.instance.deleteCartItem(getAuthToken(), id).enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (response.isSuccessful) {
                    loadCart()
                    loadSummary()
                }
            }
            override fun onFailure(call: Call<CartResponse>, t: Throwable) {}
        })
    }

    private fun clearAllCart() {
        ApiClient.instance.clearCart(getAuthToken()).enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (response.isSuccessful) {
                    loadCart()
                    loadSummary()
                    Toast.makeText(this@CartActivity, "Keranjang dibersihkan", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<CartResponse>, t: Throwable) {}
        })
    }
}