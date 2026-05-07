package com.project.matchone.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

// Import wajib karena layout dan LoginActivity sekarang berada di luar package ui/main
import com.project.matchone.R
import com.project.matchone.ui.auth.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Tahan 3 detik (3000ms), lalu pindah ke Halaman Login
        Handler(Looper.getMainLooper()).postDelayed({
            // Intent dari Sini (Splash) -> Ke Sana (Login)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // finish() sangat penting di sini agar saat user menekan tombol 'Back'
            // di halaman Login, mereka tidak kembali melihat layar Splash.
            finish()
        }, 3000)
    }
}