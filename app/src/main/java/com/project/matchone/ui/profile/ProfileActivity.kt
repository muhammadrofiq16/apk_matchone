package com.project.matchone.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.project.matchone.R
import com.project.matchone.data.model.UserModel
import com.project.matchone.data.network.ApiClient
import com.project.matchone.ui.auth.LoginActivity
import com.project.matchone.ui.checkout.CartActivity
import com.project.matchone.ui.main.CatalogActivity
import com.project.matchone.ui.main.HomeActivity
import com.project.matchone.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sessionManager = SessionManager(this)

        // --- INISIALISASI VIEW ---
        val tvProfileName  = findViewById<TextView>(R.id.tvProfileName)
        val tvLevel        = findViewById<TextView>(R.id.tvLevel)
        val tvEditProfil   = findViewById<TextView>(R.id.tvEditProfil)

        val btnGabung      = findViewById<MaterialButton>(R.id.btnGabungWhatsapp)
        val btnLogout      = findViewById<MaterialButton>(R.id.btnLogout)

        // Section Akun
        val itemKotakMasuk = findViewById<LinearLayout>(R.id.itemKotakMasuk)
        val itemAlamat     = findViewById<LinearLayout>(R.id.itemAlamat)
        val itemScan       = findViewById<LinearLayout>(R.id.itemScan)
        val itemBahasa     = findViewById<LinearLayout>(R.id.itemBahasa)

        // Section Pesan
        val btnHistoryOrder       = findViewById<LinearLayout>(R.id.btnHistoryOrder)
        val itemMetodePembayaran  = findViewById<LinearLayout>(R.id.itemMetodePembayaran)
        val itemBulkOrder         = findViewById<LinearLayout>(R.id.itemBulkOrder)

        // Section Matcha Lifestyle
        val itemBantuan          = findViewById<LinearLayout>(R.id.itemBantuan)
        val itemKebijakanPrivasi = findViewById<LinearLayout>(R.id.itemKebijakanPrivasi)
        val itemKetentuan        = findViewById<LinearLayout>(R.id.itemKetentuan)
        val itemLaporMasalah     = findViewById<LinearLayout>(R.id.itemLaporMasalah)
        val itemWhatsapp         = findViewById<LinearLayout>(R.id.itemWhatsapp)
        val itemTentang          = findViewById<LinearLayout>(R.id.itemTentang)

        // --- LOAD DATA PROFIL ---
        loadUserProfile(tvProfileName, tvLevel)

        // --- EDIT PROFIL ---
        tvEditProfil.setOnClickListener {
            Toast.makeText(this, "Fitur Edit Profil segera hadir!", Toast.LENGTH_SHORT).show()
        }

        // --- GABUNG WHATSAPP ---
        btnGabung.setOnClickListener {
            Toast.makeText(this, "Menghubungkan ke WhatsApp...", Toast.LENGTH_SHORT).show()
        }

        // --- AKUN ---
        itemKotakMasuk.setOnClickListener {
            Toast.makeText(this, "Kotak Masuk", Toast.LENGTH_SHORT).show()
        }
        itemAlamat.setOnClickListener {
            Toast.makeText(this, "Alamat Pengiriman", Toast.LENGTH_SHORT).show()
        }
        itemScan.setOnClickListener {
            Toast.makeText(this, "Scan Merchandise", Toast.LENGTH_SHORT).show()
        }
        itemBahasa.setOnClickListener {
            Toast.makeText(this, "Ubah Bahasa Aplikasi", Toast.LENGTH_SHORT).show()
        }

        // --- PESAN ---
        btnHistoryOrder.setOnClickListener {
            Toast.makeText(this, "Riwayat Pesanan", Toast.LENGTH_SHORT).show()
        }
        itemMetodePembayaran.setOnClickListener {
            Toast.makeText(this, "Metode Pembayaran", Toast.LENGTH_SHORT).show()
        }
        itemBulkOrder.setOnClickListener {
            Toast.makeText(this, "Pesanan Jumlah Besar", Toast.LENGTH_SHORT).show()
        }

        // --- MATCHA LIFESTYLE ---
        itemBantuan.setOnClickListener {
            Toast.makeText(this, "Bantuan", Toast.LENGTH_SHORT).show()
        }
        itemKebijakanPrivasi.setOnClickListener {
            Toast.makeText(this, "Kebijakan Privasi", Toast.LENGTH_SHORT).show()
        }
        itemKetentuan.setOnClickListener {
            Toast.makeText(this, "Ketentuan Layanan", Toast.LENGTH_SHORT).show()
        }
        itemLaporMasalah.setOnClickListener {
            Toast.makeText(this, "Lapor Masalah", Toast.LENGTH_SHORT).show()
        }
        itemWhatsapp.setOnClickListener {
            Toast.makeText(this, "Layanan WhatsApp", Toast.LENGTH_SHORT).show()
        }
        itemTentang.setOnClickListener {
            Toast.makeText(this, "Tentang Matcha Lifestyle", Toast.LENGTH_SHORT).show()
        }

        // --- LOGOUT ---
        btnLogout.setOnClickListener {
            logout()
        }

        // --- BOTTOM NAV ---
        setupBottomNav()
    }

    private fun loadUserProfile(tvName: TextView, tvLevel: TextView) {
        val token = "Bearer ${sessionManager.fetchAuthToken()}"
        ApiClient.instance.getUserProfile(token).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    tvName.text = user.name
                    tvLevel.text = when {
                        user.points >= 1000 -> "Ceremonial"
                        user.points >= 500  -> "Premium"
                        user.points >= 100  -> "Culinary"
                        else                -> "Starter"
                    }
                }
            }
            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                // Biarkan nama default jika gagal
            }
        })
    }

    private fun logout() {
        val token = "Bearer ${sessionManager.fetchAuthToken()}"
        ApiClient.instance.logoutUser(token).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                sessionManager.clearSession()
                moveToLogin()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                sessionManager.clearSession()
                moveToLogin()
            }
        })
    }

    private fun setupBottomNav() {
        // Highlight tab Profile aktif
        try {
            findViewById<TextView>(R.id.iconProfile)
                .setTextColor(android.graphics.Color.parseColor("#2D5A27"))
            findViewById<TextView>(R.id.textProfile)
                .setTextColor(android.graphics.Color.parseColor("#2D5A27"))
        } catch (e: Exception) {
            // bottom nav mungkin tidak ada di layout ini
        }

        findViewById<LinearLayout>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.navCatalog)?.setOnClickListener {
            startActivity(Intent(this, CatalogActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.navCart)?.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        // navProfile sudah aktif di halaman ini, tidak perlu listener
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}