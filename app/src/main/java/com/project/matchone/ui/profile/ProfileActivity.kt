package com.project.matchone.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.matchone.R
import com.project.matchone.ui.auth.LoginActivity
import com.project.matchone.ui.main.MainActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // --- 1. INISIALISASI VIEW ---
        val tvPoints = findViewById<TextView>(R.id.tvPoints)
        val etName = findViewById<EditText>(R.id.etProfileName)
        val etEmail = findViewById<EditText>(R.id.etProfileEmail)
        val etPhone = findViewById<EditText>(R.id.etProfilePhone)

        val btnSaveProfile = findViewById<Button>(R.id.btnSaveProfile)
        val btnHistoryOrder = findViewById<Button>(R.id.btnHistoryOrder)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val navHome = findViewById<ImageView>(R.id.navHome)
        val navCart = findViewById<ImageView>(R.id.navCart)

        // ==========================================
        // [TAMPILKAN DATA SAAT HALAMAN DIBUKA]
        // Nanti kamu ambil data dari SessionManager/SharedPreferences
        // ==========================================

        // --- 2. LOGIC SIMPAN PERUBAHAN (PUT UPDATE PROFILE) ---
        btnSaveProfile.setOnClickListener {
            val updatedName = etName.text.toString().trim()
            val updatedEmail = etEmail.text.toString().trim()
            val updatedPhone = etPhone.text.toString().trim()

            if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedPhone.isEmpty()) {
                Toast.makeText(this, "Data tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // [Nanti pasang logic Retrofit Update Profile di sini]
            Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
        }

        // --- 3. LOGIC TOMBOL RIWAYAT ---
        btnHistoryOrder.setOnClickListener {
            Toast.makeText(this, "Buka Riwayat Pesanan", Toast.LENGTH_SHORT).show()
        }

        // --- 4. LOGIC TOMBOL LOGOUT (DELETE) ---
        btnLogout.setOnClickListener {

            // ==========================================
            // [LOGIC API LARAVEL NANTINYA]
            // Sesuai Postman: Gunakan method DELETE dan kirim Token
            //
            // val token = "Bearer " + sessionManager.getToken()
            // ApiClient.instance.logoutUser(token).enqueue(object : Callback<LogoutResponse> {
            //      override fun onResponse(...) {
            //          // 1. Hapus token dari HP agar benar-benar keluar
            //          // sessionManager.clearSession()
            //
            //          // 2. Arahkan kembali ke LoginActivity (kode Intent di bawah)
            //      }
            // })
            // ==========================================

            // Simulasi eksekusi setelah API membalas sukses
            Toast.makeText(this, "Berhasil keluar akun", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            // Ini wajib agar user tidak bisa menekan tombol 'back' untuk kembali ke profil setelah logout
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // --- 5. LOGIC NAVIGASI BAWAH ---
        navHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        //navCart.setOnClickListener {
          //  val intent = Intent(this, CartActivity::class.java)
            //startActivity(intent)
            //finish()
        //}
    }
}