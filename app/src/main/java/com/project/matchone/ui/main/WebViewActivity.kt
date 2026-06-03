package com.project.matchone.ui.main

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.project.matchone.R

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val title   = intent.getStringExtra("TITLE") ?: "MatchOne"
        val url     = intent.getStringExtra("URL") ?: "https://www.google.com"

        val tvTitle     = findViewById<TextView>(R.id.tvWebTitle)
        val btnBack     = findViewById<ImageButton>(R.id.btnWebBack)
        val webView     = findViewById<WebView>(R.id.webView)

        tvTitle.text = title
        btnBack.setOnClickListener { finish() }

        webView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl(url)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
    }
}