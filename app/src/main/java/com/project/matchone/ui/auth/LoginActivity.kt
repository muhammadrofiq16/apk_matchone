package com.project.matchone.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.project.matchone.R
import com.project.matchone.data.model.LoginResponse
import com.project.matchone.data.network.ApiClient
import com.project.matchone.ui.main.MainActivity
import com.project.matchone.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    // --- Variabel untuk Google Sign In ---
    private lateinit var googleSignInClient: GoogleSignInClient

    // --- Penampung hasil setelah user memilih akun Google ---
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)

                // Ambil ID Token dan Email dari Google
                val idToken = account.idToken
                val email = account.email

                Log.d("GOOGLE_LOGIN", "Berhasil! Email: $email, Token: $idToken")
                Toast.makeText(this, "Selamat Datang, ${account.displayName}!", Toast.LENGTH_SHORT).show()

                // --- UPDATE: Logika Pindah ke Home ---

                // 1. Simpan sesi sementara agar terbaca 'Sudah Login'
                sessionManager.saveAuthToken("google_dummy_token_$idToken")

                // 2. Pindah ke MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                // 3. Tutup halaman login agar tidak bisa di-back
                finish()

            } catch (e: ApiException) {
                Log.w("GOOGLE_LOGIN", "Google sign in failed", e)
                Toast.makeText(this, "Google Sign In Gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        // Jika sudah login, langsung ke MainActivity
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // --- 1. INISIALISASI VIEW ---
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoogle = findViewById<Button>(R.id.btnGoogle)
        val btnFacebook = findViewById<Button>(R.id.btnFacebook)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        // --- KONFIGURASI GOOGLE SIGN IN (INI TEMPAT YANG BENAR!) ---
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("100312096114-eaalr7k6eka0352dkj0l2eegi3e9o7oq.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // --- 2. LOGIC TOMBOL LOGIN UTAMA ---
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call API Login
            ApiClient.instance.loginUser(email, password).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!

                        val token = loginResponse.token

                        if (token != null) {
                            sessionManager.saveAuthToken(token)
                            Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Token tidak ditemukan dalam response", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login Gagal: Cek email/password", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // --- 3. LOGIC TOMBOL SOCIAL LOGIN ---
        btnGoogle.setOnClickListener {
            // Memanggil popup pilihan akun Google
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        btnFacebook.setOnClickListener {
            Toast.makeText(this, "Fitur Login Facebook sedang dikembangkan!", Toast.LENGTH_SHORT).show()
        }

        // --- 4. LOGIC TULISAN "SIGNUP" ---
        tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}