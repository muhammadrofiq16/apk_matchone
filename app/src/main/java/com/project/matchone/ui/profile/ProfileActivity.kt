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
import com.project.matchone.ui.main.WebViewActivity
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
            val nomorWA = "6281234567890" // ganti nomor WhatsApp bisnis kamu
            val intent = Intent(
                Intent.ACTION_VIEW,
                android.net.Uri.parse("https://wa.me/$nomorWA?text=Halo%20MatchOne%2C%20saya%20ingin%20bergabung!")
            )
            startActivity(intent)
        }

        // --- AKUN ---
        itemKotakMasuk.setOnClickListener {
            Toast.makeText(this, "Fitur Kotak Masuk segera hadir!", Toast.LENGTH_SHORT).show()
        }
        itemAlamat.setOnClickListener {
            Toast.makeText(this, "Fitur Alamat Pengiriman segera hadir!", Toast.LENGTH_SHORT).show()
        }
        itemScan.setOnClickListener {
            Toast.makeText(this, "Fitur Scan Merchandise segera hadir!", Toast.LENGTH_SHORT).show()
        }
        itemBahasa.setOnClickListener {
            Toast.makeText(this, "Fitur Ubah Bahasa segera hadir!", Toast.LENGTH_SHORT).show()
        }

        // --- PESAN ---
        btnHistoryOrder.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        itemMetodePembayaran.setOnClickListener {
            Toast.makeText(this, "Fitur Metode Pembayaran segera hadir!", Toast.LENGTH_SHORT).show()
        }
        itemBulkOrder.setOnClickListener {
            Toast.makeText(this, "Fitur Pesanan Jumlah Besar segera hadir!", Toast.LENGTH_SHORT).show()
        }

        // --- MATCHA LIFESTYLE ---
        itemBantuan.setOnClickListener {
            startActivity(
                Intent(this, WebViewActivity::class.java).apply {
                    putExtra("TITLE", "Bantuan")
                    putExtra("URL", "https://matchone.com/help") // ganti URL kamu
                }
            )
        }

        itemKebijakanPrivasi.setOnClickListener {
            startActivity(
                Intent(this, WebViewActivity::class.java).apply {
                    putExtra("TITLE", "Kebijakan Privasi")
                    putExtra("URL", "https://matchone.com/privacy") // ganti URL kamu
                }
            )
        }

        itemKetentuan.setOnClickListener {
            startActivity(
                Intent(this, WebViewActivity::class.java).apply {
                    putExtra("TITLE", "Ketentuan Layanan")
                    putExtra("URL", "https://matchone.com/terms") // ganti URL kamu
                }
            )
        }

        itemLaporMasalah.setOnClickListener {
            val nomorWA = "6281234567890" // ganti nomor WhatsApp bisnis kamu
            val intent = Intent(
                Intent.ACTION_VIEW,
                android.net.Uri.parse("https://wa.me/$nomorWA?text=Halo%20MatchOne%2C%20saya%20ingin%20melaporkan%20masalah!")
            )
            startActivity(intent)
        }

        itemWhatsapp.setOnClickListener {
            val nomorWA = "6281234567890" // ganti nomor WhatsApp bisnis kamu
            val intent = Intent(
                Intent.ACTION_VIEW,
                android.net.Uri.parse("https://wa.me/$nomorWA?text=Halo%20MatchOne%2C%20saya%20butuh%20bantuan!")
            )
            startActivity(intent)
        }

        itemTentang.setOnClickListener {
            startActivity(
                Intent(this, WebViewActivity::class.java).apply {
                    putExtra("TITLE", "Tentang Matcha Lifestyle")
                    putExtra("URL", "https://matchone.com/about") // ganti URL kamu
                }
            )
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
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}