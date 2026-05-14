package com.project.matchone.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.project.matchone.utils.CartRepository
import com.project.matchone.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            moveToLogin()
            return
        }

        val token = sessionManager.fetchAuthToken() ?: ""
        cartRepository = CartRepository(token)

        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navCart = findViewById<LinearLayout>(R.id.navCart)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        rvCategories = findViewById(R.id.rvCategories)
        rvProducts = findViewById(R.id.rvProducts)

        layoutMiniCart = findViewById(R.id.layoutMiniCart)
        tvMiniCartCount = findViewById(R.id.tvMiniCartCount)
        tvMiniCartTotal = findViewById(R.id.tvMiniCartTotal)

        rvCategories.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        rvProducts.layoutManager =
            GridLayoutManager(this, 2)

        fetchCategories()
        fetchProducts()
        loadMiniCart()

        navHome.setOnClickListener {
            rvProducts.smoothScrollToPosition(0)
        }

        navCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        layoutMiniCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        navProfile.setOnClickListener {
            showLogoutDialog()
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

        ApiClient.instance.getCartSummary(token)
            .enqueue(object : Callback<CartSummary> {

                override fun onResponse(
                    call: Call<CartSummary>,
                    response: Response<CartSummary>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val summary = response.body()!!.data

                        val totalQty = summary.total_quantity
                        val totalPrice = summary.total_price

                        if (totalQty > 0) {
                            layoutMiniCart.visibility = View.VISIBLE
                            tvMiniCartCount.text = "$totalQty produk"

                            val localeID = Locale("in", "ID")
                            val formatRupiah =
                                NumberFormat.getCurrencyInstance(localeID)

                            tvMiniCartTotal.text = formatRupiah
                                .format(totalPrice)
                                .replace("Rp", "Rp ")
                        } else {
                            layoutMiniCart.visibility = View.GONE
                        }
                    } else {
                        layoutMiniCart.visibility = View.GONE
                    }
                }

                override fun onFailure(
                    call: Call<CartSummary>,
                    t: Throwable
                ) {
                    layoutMiniCart.visibility = View.GONE
                }
            })
    }

    private fun fetchCategories() {
        ApiClient.instance.getCategories()
            .enqueue(object : Callback<CategoryResponse> {

                override fun onResponse(
                    call: Call<CategoryResponse>,
                    response: Response<CategoryResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val categories = response.body()!!.categories

                        adapterCategory = CategoryAdapter(categories) { category ->
                            fetchProducts(category.id)
                        }

                        rvCategories.adapter = adapterCategory
                    }
                }

                override fun onFailure(
                    call: Call<CategoryResponse>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@MainActivity,
                        "Gagal konek: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun fetchProducts(categoryId: Int? = null) {
        val call = if (categoryId == null) {
            ApiClient.instance.getProducts()
        } else {
            ApiClient.instance.getProductsByCategory(categoryId)
        }

        call.enqueue(object : Callback<ProductResponse> {

            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val products = response.body()!!.products

                    adapterMenu = MenuAdapter(products) { clickedProduct ->
                        addToCart(clickedProduct)
                    }

                    rvProducts.adapter = adapterMenu
                }
            }

            override fun onFailure(
                call: Call<ProductResponse>,
                t: Throwable
            ) {
                Toast.makeText(
                    this@MainActivity,
                    "Error produk: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun addToCart(product: ProductModel) {
        val productId = product.id ?: return

        cartRepository.addToCart(
            productId = productId,
            quantity = 1,
            onSuccess = {
                Toast.makeText(
                    this@MainActivity,
                    "${product.name} berhasil dimasukkan ke keranjang!",
                    Toast.LENGTH_SHORT
                ).show()

                loadMiniCart()
            },
            onError = { msg ->
                Toast.makeText(
                    this@MainActivity,
                    msg,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                logout()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun logout() {
        val token = "Bearer ${sessionManager.fetchAuthToken()}"

        ApiClient.instance.logoutUser(token)
            .enqueue(object : Callback<Void> {

                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    sessionManager.clearSession()
                    moveToLogin()
                }

                override fun onFailure(
                    call: Call<Void>,
                    t: Throwable
                ) {
                    sessionManager.clearSession()
                    moveToLogin()
                }
            })
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}