package com.project.matchone.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.project.matchone.R
import com.project.matchone.ui.auth.LoginActivity
import com.project.matchone.ui.checkout.CartActivity
import com.project.matchone.utils.SessionManager

class HomeActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            moveToLogin()
            return
        }

        setupImages()
        setupButtons()
        setupBottomNavigation()
    }

    private fun setupImages() {
        val ivHeroImage = findViewById<ImageView>(R.id.ivHeroImage)
        val ivUjiGold = findViewById<ImageView>(R.id.ivUjiGold)
        val ivLatte = findViewById<ImageView>(R.id.ivLatte)
        val ivBrewingGuide = findViewById<ImageView>(R.id.ivBrewingGuide)

        // Hero Image
        Glide.with(this)
            .load("https://lh3.googleusercontent.com/aida-public/AB6AXuCp_dwlb0YTYr7KVuhuXXJoeTcssl6zH2KKUjHck-GE8UZW3DBIzRfj4AhEsjRYmtCLCUgL7x_QquPYfJkLUsZrA2JZW9WSec14FcJj_iawXf82twpN5HPkYFBfA597TI6qgUMOL8IRa8DDRxJUwolVGnlSHjhauUtKeJ8thwj4VtIH59XeIIyZ2Y5KlukvaqEI-NfQimkhjuAiuG-w4SGIJABvFspL3DjOCfn2JeFmBoWiyugKHOIrqYwxfvbLAd8vWW93hDf7SWgf")
            .into(ivHeroImage)

        // Uji Gold Image
        Glide.with(this)
            .load("https://images.unsplash.com/photo-1515823064-24564c70d4fc?q=80&w=2070&auto=format&fit=crop")
            .into(ivUjiGold)

        // Latte Image
        Glide.with(this)
            .load("https://images.unsplash.com/photo-1536584754829-12214d404f52?q=80&w=2070&auto=format&fit=crop")
            .into(ivLatte)

        // Brewing Guide Image
        Glide.with(this)
            .load("https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?q=80&w=1974&auto=format&fit=crop")
            .into(ivBrewingGuide)
    }

    private fun setupButtons() {
        val btnViewAll = findViewById<TextView>(R.id.btnViewAll)
        val btnExploreBlends = findViewById<Button>(R.id.btnExploreBlends)
        val btnAddToCartGold = findViewById<Button>(R.id.btnAddToCartGold)
        val btnHeaderCart = findViewById<ImageView>(R.id.btnHeaderCart)

        val navigateToCatalog = {
            startActivity(Intent(this, CatalogActivity::class.java))
            finish()
        }

        btnViewAll.setOnClickListener { navigateToCatalog() }
        btnExploreBlends.setOnClickListener { navigateToCatalog() }
        btnAddToCartGold.setOnClickListener { navigateToCatalog() }

        btnHeaderCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navCatalog = findViewById<LinearLayout>(R.id.navCatalog)
        val navCart = findViewById<LinearLayout>(R.id.navCart)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        // Update UI state for active tab (Home)
        findViewById<TextView>(R.id.iconHome).setTextColor(android.graphics.Color.parseColor("#37563b"))
        findViewById<TextView>(R.id.textHome).setTextColor(android.graphics.Color.parseColor("#37563b"))

        navHome.setOnClickListener {
            // Already in Home
        }

        navCatalog.setOnClickListener {
            startActivity(Intent(this, CatalogActivity::class.java))
            finish()
        }

        navCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        navProfile.setOnClickListener {
            // Asumsikan ada ProfileActivity. Karena sebelumnya MainActivity menangani profile, 
            // kita arahkan ke CatalogActivity sementara jika ProfileActivity belum ada,
            // atau jika sudah ada biarkan. 
            // Kita arahkan ke Catalog karena CatalogActivity punya fungsi showLogoutDialog
            startActivity(Intent(this, CatalogActivity::class.java).apply {
                putExtra("action", "profile")
            })
            finish()
        }
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
