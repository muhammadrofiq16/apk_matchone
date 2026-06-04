package com.project.matchone.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        val btnBack              = findViewById<ImageButton>(R.id.btnBack)
        val tvProfileName        = findViewById<TextView>(R.id.tvProfileName)
        val tvProfileEmail       = findViewById<TextView>(R.id.tvProfileEmail)
        val itemUbahPassword     = findViewById<LinearLayout>(R.id.itemUbahPassword)
        val btnHistoryOrder      = findViewById<LinearLayout>(R.id.btnHistoryOrder)
        val switchNotifikasi     = findViewById<Switch>(R.id.switchNotifikasi)
        val itemKebijakanPrivasi = findViewById<LinearLayout>(R.id.itemKebijakanPrivasi)
        val itemBantuan          = findViewById<LinearLayout>(R.id.itemBantuan)
        val btnLogout            = findViewById<MaterialButton>(R.id.btnLogout)

        // --- LOAD PROFIL ---
        loadUserProfile(tvProfileName, tvProfileEmail)

        // --- BACK ---
        btnBack.setOnClickListener { finish() }

        // --- KEAMANAN ---
        itemUbahPassword.setOnClickListener {
            Toast.makeText(this, "Fitur Ubah Kata Sandi segera hadir!", Toast.LENGTH_SHORT).show()
        }

        // --- PESANAN: Riwayat Pesanan ---
        btnHistoryOrder.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // --- PREFERENSI ---
        switchNotifikasi.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Notifikasi diaktifkan" else "Notifikasi dimatikan"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // --- INFORMASI ---
        itemKebijakanPrivasi.setOnClickListener {
            Toast.makeText(this, "Kebijakan Privasi", Toast.LENGTH_SHORT).show()
        }
        itemBantuan.setOnClickListener {
            Toast.makeText(this, "Pusat Bantuan", Toast.LENGTH_SHORT).show()
        }

        // --- LOGOUT ---
        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Keluar Akun")
                .setMessage("Apakah kamu yakin ingin keluar?")
                .setPositiveButton("Ya, Keluar") { _, _ -> logout() }
                .setNegativeButton("Batal", null)
                .show()
        }
        // --- BOTTOM NAV ---
        setupBottomNav()
    }

    private fun loadUserProfile(tvName: TextView, tvEmail: TextView) {
        val token = "Bearer ${sessionManager.fetchAuthToken()}"
        ApiClient.instance.getUserProfile(token).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    tvName.text  = user.name
                    tvEmail.text = user.email
                }
            }
            override fun onFailure(call: Call<UserModel>, t: Throwable) { }
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
        } catch (e: Exception) { /* ignore */ }

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