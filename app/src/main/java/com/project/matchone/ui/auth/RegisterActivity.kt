package com.project.matchone.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.matchone.R
import com.project.matchone.data.model.LoginResponse
import com.project.matchone.data.network.ApiClient
import com.project.matchone.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sessionManager = SessionManager(this)

        // --- 1. INISIALISASI TOMBOL ---
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val tvBackToLogin = findViewById<TextView>(R.id.tvBackToLogin)

        // --- 2. INISIALISASI INPUT FORM (Sesuai JSON Postman) ---
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPasswordConfirm = findViewById<EditText>(R.id.etPasswordConfirm)

        // --- 3. LOGIC TOMBOL SIGN UP ---
        btnSignUp.setOnClickListener {
            // Ambil teks dari inputan user
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val passwordConfirm = etPasswordConfirm.text.toString().trim()

            // Validasi: Cek apakah ada yang kosong
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener 
            }

            // Validasi: Cek apakah password dan konfirmasi sama
            if (password != passwordConfirm) {
                Toast.makeText(this, "Password dan Konfirmasi Password tidak cocok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call API Register
            ApiClient.instance.registerUser(name, email, phone, password, passwordConfirm)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful && response.body() != null) {
                            Toast.makeText(this@RegisterActivity, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, "Registrasi Gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        // --- 4. LOGIC TULISAN "Back to Login" ---
        tvBackToLogin.setOnClickListener {
            finish()
        }
    }
}
