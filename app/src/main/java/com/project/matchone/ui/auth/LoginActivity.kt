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
import com.project.matchone.ui.main.HomeActivity
import com.project.matchone.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (idToken == null) {
                    Toast.makeText(this, "Gagal mendapatkan token Google", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }

                Log.d("GOOGLE_LOGIN", "idToken didapat, mengirim ke server...")

                // ✅ Kirim Google token ke Laravel untuk ditukar Sanctum token
                exchangeGoogleToken(idToken)

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

        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        val etEmail    = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin   = findViewById<Button>(R.id.btnLogin)
        val btnGoogle  = findViewById<Button>(R.id.btnGoogle)
        val btnFacebook   = findViewById<Button>(R.id.btnFacebook)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("100312096114-eaalr7k6eka0352dkj0l2eegi3e9o7oq.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Login email + password
        btnLogin.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ApiClient.instance.loginUser(email, password).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val token = response.body()!!.token
                        if (token != null) {
                            sessionManager.saveAuthToken(token)
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
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

        // Login Google
        btnGoogle.setOnClickListener {
            googleSignInClient.signInIntent.also { googleSignInLauncher.launch(it) }
        }

        btnFacebook.setOnClickListener {
            Toast.makeText(this, "Fitur Login Facebook sedang dikembangkan!", Toast.LENGTH_SHORT).show()
        }

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * Kirim Google idToken ke Laravel → dapat Sanctum token → simpan ke SessionManager
     */
    private fun exchangeGoogleToken(idToken: String) {
        ApiClient.instance.googleLogin(idToken).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val sanctumToken = response.body()!!.token

                    if (sanctumToken != null) {
                        // ✅ Simpan Sanctum token, bukan Google token
                        sessionManager.saveAuthToken(sanctumToken)

                        Log.d("GOOGLE_LOGIN", "Sanctum token tersimpan: $sanctumToken")
                        Toast.makeText(this@LoginActivity, "Login Google berhasil!", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Token dari server tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("GOOGLE_LOGIN", "Response gagal: ${response.code()} ${response.errorBody()?.string()}")
                    Toast.makeText(this@LoginActivity, "Login Google gagal di server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("GOOGLE_LOGIN", "Koneksi gagal: ${t.message}")
                Toast.makeText(this@LoginActivity, "Koneksi bermasalah: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
