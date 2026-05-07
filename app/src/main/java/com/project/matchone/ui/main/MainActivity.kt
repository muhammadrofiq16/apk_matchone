package com.project.matchone.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.matchone.R
import com.project.matchone.adapter.CategoryAdapter
import com.project.matchone.adapter.MenuAdapter
import com.project.matchone.data.model.CategoryResponse
import com.project.matchone.data.model.ProductResponse
import com.project.matchone.data.network.ApiClient
import com.project.matchone.ui.auth.LoginActivity
import com.project.matchone.ui.profile.ProfileActivity
import com.project.matchone.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var rvCategories: RecyclerView
    private lateinit var rvProducts: RecyclerView
    private lateinit var adapterCategory: CategoryAdapter
    private lateinit var adapterMenu: MenuAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        // Cek apakah user sudah login, jika tidak lempar ke LoginActivity
        if (!sessionManager.isLoggedIn()) {
            moveToLogin()
        }

        // --- 1. INISIALISASI VIEW ---
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navCart = findViewById<LinearLayout>(R.id.navCart)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        rvCategories = findViewById(R.id.rvCategories)
        rvProducts = findViewById(R.id.rvProducts)

        // LayoutManager: Kategori (Horizontal), Produk (Grid 2 Kolom)
        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvProducts.layoutManager = GridLayoutManager(this, 2)

        // --- 2. AMBIL DATA DARI API ---
        fetchCategories()
        fetchProducts()

        // --- 3. NAVIGASI ---
        navProfile.setOnClickListener {
            // Contoh implementasi logout sederhana atau pindah ke profile
            showLogoutDialog()
        }
        
        // navCart.setOnClickListener { ... }
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
                Toast.makeText(this@MainActivity, "Gagal konek: ${t.message}", Toast.LENGTH_SHORT).show()
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
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val products = response.body()!!.products
                    adapterMenu = MenuAdapter(products)
                    rvProducts.adapter = adapterMenu
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error produk: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
        val token = "Bearer " + sessionManager.fetchAuthToken()
        ApiClient.instance.logoutUser(token).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Tetap hapus session lokal meskipun API logout gagal (misal token expired)
                sessionManager.clearSession()
                moveToLogin()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                sessionManager.clearSession()
                moveToLogin()
            }
        })
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
