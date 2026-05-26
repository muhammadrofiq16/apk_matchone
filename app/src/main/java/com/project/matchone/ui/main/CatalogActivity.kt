package com.project.matchone.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.matchone.R
import com.project.matchone.adapter.CategoryAdapter
import com.project.matchone.adapter.MenuAdapter
import com.project.matchone.data.model.CartSummary
import com.project.matchone.data.model.CategoryResponse
import com.project.matchone.data.model.ProductModel
import com.project.matchone.data.model.ProductResponse
import com.project.matchone.data.network.ApiClient
import com.project.matchone.ui.auth.LoginActivity
import com.project.matchone.ui.checkout.CartActivity
import com.project.matchone.ui.profile.ProfileActivity
import com.project.matchone.utils.CartRepository
import com.project.matchone.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class CatalogActivity : AppCompatActivity() {

    private lateinit var rvCategories: RecyclerView
    private lateinit var rvProducts: RecyclerView
    private lateinit var adapterCategory: CategoryAdapter
    private lateinit var adapterMenu: MenuAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var cartRepository: CartRepository

    private lateinit var layoutMiniCart: View
    private lateinit var tvMiniCartCount: TextView
    private lateinit var tvMiniCartTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            moveToLogin()
            return
        }

        val token = sessionManager.fetchAuthToken() ?: ""
        cartRepository = CartRepository(token)

        val navHome    = findViewById<LinearLayout>(R.id.navHome)
        val navCatalog = findViewById<LinearLayout>(R.id.navCatalog)
        val navCart    = findViewById<LinearLayout>(R.id.navCart)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        // Highlight tab Catalog aktif
        findViewById<TextView>(R.id.iconCatalog).setTextColor(android.graphics.Color.parseColor("#4CAF50"))
        findViewById<TextView>(R.id.textCatalog).setTextColor(android.graphics.Color.parseColor("#4CAF50"))

        rvCategories = findViewById(R.id.rvCategories)
        rvProducts   = findViewById(R.id.rvProducts)

        layoutMiniCart   = findViewById(R.id.layoutMiniCart)
        tvMiniCartCount  = findViewById(R.id.tvMiniCartCount)
        tvMiniCartTotal  = findViewById(R.id.tvMiniCartTotal)

        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvProducts.layoutManager   = GridLayoutManager(this, 2)

        fetchCategories()
        fetchProducts()
        loadMiniCart()

        navHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        navCatalog.setOnClickListener {
            rvProducts.smoothScrollToPosition(0)
        }

        navCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        layoutMiniCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // ✅ FIX: Arahkan ke ProfileActivity langsung
        navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (::cartRepository.isInitialized) {
            loadMiniCart()
        }
    }

    private fun loadMiniCart() {
        val token = "Bearer ${sessionManager.fetchAuthToken()}"
        ApiClient.instance.getCartSummary(token).enqueue(object : Callback<CartSummary> {
            override fun onResponse(call: Call<CartSummary>, response: Response<CartSummary>) {
                if (response.isSuccessful && response.body() != null) {
                    val summary = response.body()!!.data
                    if (summary.total_quantity > 0) {
                        layoutMiniCart.visibility = View.VISIBLE
                        tvMiniCartCount.text = "${summary.total_quantity} produk"
                        val localeID = Locale("in", "ID")
                        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                        tvMiniCartTotal.text = formatRupiah.format(summary.total_price).replace("Rp", "Rp ")
                    } else {
                        layoutMiniCart.visibility = View.GONE
                    }
                } else {
                    layoutMiniCart.visibility = View.GONE
                }
            }
            override fun onFailure(call: Call<CartSummary>, t: Throwable) {
                layoutMiniCart.visibility = View.GONE
            }
        })
    }

    private fun fetchCategories() {
        ApiClient.instance.getCategories().enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val categories = response.body()!!.categories
                    adapterCategory = CategoryAdapter(categories) { category ->
                        fetchProducts(category.id)
                    }
                    rvCategories.adapter = adapterCategory
                }
            }
            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                Toast.makeText(this@CatalogActivity, "Gagal konek: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchProducts(categoryId: Int? = null) {
        val call = if (categoryId == null) ApiClient.instance.getProducts()
        else ApiClient.instance.getProductsByCategory(categoryId)

        call.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    adapterMenu = MenuAdapter(response.body()!!.products) { product ->
                        addToCart(product)
                    }
                    rvProducts.adapter = adapterMenu
                }
            }
            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Toast.makeText(this@CatalogActivity, "Error produk: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addToCart(product: ProductModel) {
        cartRepository.addToCart(
            productId = product.id,
            quantity  = 1,
            onSuccess = {
                Toast.makeText(this, "${product.name} berhasil dimasukkan ke keranjang!", Toast.LENGTH_SHORT).show()
                loadMiniCart()
            },
            onError = { msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}