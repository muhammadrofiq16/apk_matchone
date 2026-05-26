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
import com.project.matchone.ui.checkout.CartActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pastikan nama file layoutmu benar 'activity_profile'
        setContentView(R.layout.activity_profile)

        // --- 1. INISIALISASI VIEW SECARA MANUAL ---
        val tvPoints = findViewById<TextView>(R.id.tvPoints)
        val etName = findViewById<EditText>(R.id.etProfileName)
        val etEmail = findViewById<EditText>(R.id.etProfileEmail)
        val etPhone = findViewById<EditText>(R.id.etProfilePhone)

        val btnSaveProfile = findViewById<Button>(R.id.btnSaveProfile)
        val btnHistoryOrder = findViewById<Button>(R.id.btnHistoryOrder)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val navHome = findViewById<ImageView>(R.id.navHome)
        val navCart = findViewById<ImageView>(R.id.navCart)

        // --- 2. ISI DATA DUMMY ---
        tvPoints.text = "500 Poin"
        etName.setText("User MatchOne")
        etEmail.setText("user@example.com")
        etPhone.setText("08123456789")

        // --- 3. LOGIC SIMPAN ---
        btnSaveProfile.setOnClickListener {
            Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
        }

        // --- 4. LOGIC TOMBOL RIWAYAT (MENUJU HISTORY) ---
        btnHistoryOrder.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // --- 5. LOGIC TOMBOL LOGOUT ---
        btnLogout.setOnClickListener {
            Toast.makeText(this, "Berhasil keluar akun", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // --- 6. NAVIGASI ---
        navHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        navCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }
}