package com.project.matchone.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
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
        val tvProfileName   = findViewById<TextView>(R.id.tvProfileName)
        val tvProfileEmail  = findViewById<TextView>(R.id.tvProfileEmail)
        val tvLevel         = findViewById<TextView>(R.id.tvLevel)
        val tvPoints        = findViewById<TextView>(R.id.tvPoints)
        val tvNextLevel     = findViewById<TextView>(R.id.tvNextLevel)
        val progressPoints  = findViewById<ProgressBar>(R.id.progressPoints)
        val tvEditProfil    = findViewById<TextView>(R.id.tvEditProfil)
        val btnBack         = findViewById<ImageButton>(R.id.btnBackProfile)
        val btnGabung       = findViewById<MaterialButton>(R.id.btnGabungWhatsapp)
        val btnLogout       = findViewById<MaterialButton>(R.id.btnLogout)

        // Section Akun
        val itemKotakMasuk  = findViewById<LinearLayout>(R.id.itemKotakMasuk)
        val itemAlamat      = findViewById<LinearLayout>(R.id.itemAlamat)
        val itemScan        = findViewById<LinearLayout>(R.id.itemScan)
        val itemBahasa      = findViewById<LinearLayout>(R.id.itemBahasa)

        // Section Pesan
        val btnHistoryOrder      = findViewById<LinearLayout>(R.id.btnHistoryOrder)
        val itemMetodePembayaran = findViewById<LinearLayout>(R.id.itemMetodePembayaran)
        val itemBulkOrder        = findViewById<LinearLayout>(R.id.itemBulkOrder)

        // Section Matcha Lifestyle
        val itemBantuan          = findViewById<LinearLayout>(R.id.itemBantuan)
        val itemKebijakanPrivasi = findViewById<LinearLayout>(R.id.itemKebijakanPrivasi)
        val itemKetentuan        = findViewById<LinearLayout>(R.id.itemKetentuan)
        val itemLaporMasalah     = findViewById<LinearLayout>(R.id.itemLaporMasalah)
        val itemWhatsapp         = findViewById<LinearLayout>(R.id.itemWhatsapp)
        val itemTentang          = findViewById<LinearLayout>(R.id.itemTentang)

        // --- LOAD DATA PROFIL ---
        loadUserProfile(tvProfileName, tvProfileEmail, tvLevel, tvPoints, tvNextLevel, progressPoints)

        // --- TOMBOL BACK ---
        btnBack.setOnClickListener { finish() }

        // --- EDIT PROFIL ---
        tvEditProfil.setOnClickListener {
            showEditProfilDialog(tvProfileName)
        }

        // --- GABUNG WHATSAPP ---
        btnGabung.setOnClickListener {
            val nomorWA = "6281234567890"
            startActivity(Intent(Intent.ACTION_VIEW,
                android.net.Uri.parse("https://wa.me/$nomorWA?text=Halo%20MatchOne%2C%20saya%20ingin%20bergabung!")))
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
            startActivity(Intent(this, WebViewActivity::class.java).apply {
                putExtra("TITLE", "Bantuan")
                putExtra("URL", "https://matchone.com/help")
            })
        }
        itemKebijakanPrivasi.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java).apply {
                putExtra("TITLE", "Kebijakan Privasi")
                putExtra("URL", "https://matchone.com/privacy")
            })
        }
        itemKetentuan.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java).apply {
                putExtra("TITLE", "Ketentuan Layanan")
                putExtra("URL", "https://matchone.com/terms")
            })
        }
        itemLaporMasalah.setOnClickListener {
            val nomorWA = "6281234567890"
            startActivity(Intent(Intent.ACTION_VIEW,
                android.net.Uri.parse("https://wa.me/$nomorWA?text=Halo%20MatchOne%2C%20saya%20ingin%20melaporkan%20masalah!")))
        }
        itemWhatsapp.setOnClickListener {
            val nomorWA = "6281234567890"
            startActivity(Intent(Intent.ACTION_VIEW,
                android.net.Uri.parse("https://wa.me/$nomorWA?text=Halo%20MatchOne%2C%20saya%20butuh%20bantuan!")))
        }
        itemTentang.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java).apply {
                putExtra("TITLE", "Tentang Matcha Lifestyle")
                putExtra("URL", "https://matchone.com/about")
            })
        }

        // --- LOGOUT ---
        btnLogout.setOnClickListener { logout() }

        // --- BOTTOM NAV ---
        setupBottomNav()
    }

    private fun loadUserProfile(
        tvName: TextView,
        tvEmail: TextView,
        tvLevel: TextView,
        tvPoints: TextView,
        tvNextLevel: TextView,
        progressPoints: ProgressBar
    ) {
        val token = "Bearer ${sessionManager.fetchAuthToken()}"
        ApiClient.instance.getUserProfile(token).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!

                    tvName.text  = user.name
                    tvEmail.text = user.email
                    tvPoints.text = "${user.points} poin"

                    // Tentukan level & progress
                    val levelName: String
                    val nextLevelPoints: Int
                    val maxPoints: Int

                    when {
                        user.points >= 1000 -> {
                            levelName = "Ceremonial"
                            nextLevelPoints = 0
                            maxPoints = 1000
                        }
                        user.points >= 500 -> {
                            levelName = "Premium"
                            nextLevelPoints = 1000 - user.points
                            maxPoints = 500
                        }
                        user.points >= 100 -> {
                            levelName = "Culinary"
                            nextLevelPoints = 500 - user.points
                            maxPoints = 400
                        }
                        else -> {
                            levelName = "Starter"
                            nextLevelPoints = 100 - user.points
                            maxPoints = 100
                        }
                    }

                    tvLevel.text = levelName

                    if (nextLevelPoints > 0) {
                        val nextName = when {
                            user.points >= 500 -> "Ceremonial"
                            user.points >= 100 -> "Premium"
                            else               -> "Culinary"
                        }
                        tvNextLevel.text = "$nextLevelPoints poin → $nextName"
                        progressPoints.max = maxPoints
                        progressPoints.progress = user.points % maxPoints
                    } else {
                        tvNextLevel.text = "Level Tertinggi 🎉"
                        progressPoints.progress = 100
                    }
                }
            }
            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                // Biarkan default jika gagal
            }
        })
    }

    private fun showEditProfilDialog(tvName: TextView) {
        val dialogView = layoutInflater.inflate(android.R.layout.simple_list_item_1, null)

        // Buat dialog sederhana dengan 2 input
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 32, 48, 16)
        }

        val etName = EditText(this).apply {
            hint = "Nama Lengkap"
            setText(tvName.text)
        }
        val etPhone = EditText(this).apply {
            hint = "Nomor Telepon"
            inputType = android.text.InputType.TYPE_CLASS_PHONE
        }

        layout.addView(etName)
        layout.addView(etPhone)

        AlertDialog.Builder(this)
            .setTitle("Edit Profil")
            .setView(layout)
            .setPositiveButton("Simpan") { _, _ ->
                val newName  = etName.text.toString().trim()
                val newPhone = etPhone.text.toString().trim()

                if (newName.isEmpty()) {
                    Toast.makeText(this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val token = "Bearer ${sessionManager.fetchAuthToken()}"
                ApiClient.instance.updateProfile(token, newName, newPhone)
                    .enqueue(object : Callback<UserModel> {
                        override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                            if (response.isSuccessful && response.body() != null) {
                                tvName.text = response.body()!!.name
                                Toast.makeText(this@ProfileActivity,
                                    "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@ProfileActivity,
                                    "Gagal update profil", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<UserModel>, t: Throwable) {
                            Toast.makeText(this@ProfileActivity,
                                "Koneksi bermasalah: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            .setNegativeButton("Batal", null)
            .show()
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
                .setTextColor(android.graphics.Color.parseColor("#37563b"))
            findViewById<TextView>(R.id.textProfile)
                .setTextColor(android.graphics.Color.parseColor("#37563b"))
        } catch (e: Exception) { }

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
            overridePendingTransition(R.anim.fade_scale_in, R.anim.fade_scale_out)
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
    }
}